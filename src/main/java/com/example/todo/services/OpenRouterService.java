package com.example.todo.services;

import com.example.todo.DTOs.ChatRequestDTO;
import com.example.todo.DTOs.ChatResponseDTO;
import com.example.todo.DTOs.SuggestionDTO;
import com.example.todo.enums.STATUS;
import com.example.todo.models.TaskModel;
import com.example.todo.repositories.TaskRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class OpenRouterService {
    @Value("${openrouter.api.key}")
    private String apiKey;

    @Value("${openrouter.api.url}")
    private String apiUrl;

    @Value("${openrouter.model}")
    private String model;

    @Autowired
    private TaskRepository taskRepository;

    private final Bucket bucket;

    private final RestTemplate restTemplate = new RestTemplate();

    public OpenRouterService() {
        Refill refill = Refill.intervally(1, Duration.ofSeconds(6)); // 10 requests per minute
        Bandwidth limit = Bandwidth.classic(10, refill);
        this.bucket = Bucket.builder().addLimit(limit).build();
    }

    public SuggestionDTO askQuestionWithRateLimit(String question) throws Exception {
        if (bucket.tryConsume(1)) {
            return askQuestion(question);
        } else {
            throw new Exception("Rate limit exceeded");
        }
    }


    public SuggestionDTO askQuestion(String question) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        if (email == null) {
            throw new RuntimeException("User not found");
        }

        LocalDateTime now = LocalDateTime.now();

        List<TaskModel> tasks = taskRepository.findByUserEmailAndStatusAndMonthAndYear(email, List.of(STATUS.TODO, STATUS.IN_PROGRESS), now.getMonthValue(), now.getYear());
        ChatRequestDTO request = getChatRequestDTO(question, tasks);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<ChatRequestDTO> entity = new HttpEntity<>(request, headers);

        ResponseEntity<ChatResponseDTO> response = restTemplate.exchange(
                apiUrl,
                HttpMethod.POST,
                entity,
                ChatResponseDTO.class
        );

        if (response.getBody() != null &&
                response.getBody().getChoices() != null &&
                !response.getBody().getChoices().isEmpty()) {
            return parseResponse(response.getBody().getChoices().getFirst().getMessage().getContent());
        }

        return null;
    }

    private SuggestionDTO parseResponse(String response) {
        try {
            if (Objects.equals(response, "Cannot answer this question")) {
                return null;
            }
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            return mapper.readValue(response, SuggestionDTO.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse AI response", e);
        }
    }

    private ChatRequestDTO getChatRequestDTO(String question, List<TaskModel> tasks) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastDayOfMonth = getLastDayOfMonth(now);

        String prompt = """
                You are a scheduling assistant.
                
                        HARD RULES:
                        1) Only answer if the query asks for suggested times within a specific month AND year. Otherwise return exactly: "Cannot answer this question".
                        2) Only schedule tasks INSIDE the VALID WINDOW defined below.
                        3) Never place any suggestion earlier than the VALID WINDOW START.
                        4) Distribute tasks evenly without overlapping.
                        5) Short summary show why learning at this time is the best.
                
                        OUTPUT FORMAT (JSON only):
                        {
                          "schedule": [
                            { "taskId": <number>, "title": "<string>", "suggestedStart": "YYYY-MM-DDTHH:mm", "durationMinutes": <number>, "summary": "<string>" }
                          ]
                        }
                        No prose, no markdown, JSON only.
                
                        VALID WINDOW:
                        - START: %s
                        - END:   %s
                        (Timezone: Asia/Ho_Chi_Minh)
                """.formatted(now, lastDayOfMonth);
        return new ChatRequestDTO(
                model,
                List.of(new ChatRequestDTO.Message("system", prompt), new ChatRequestDTO.Message("user", "Tasks: " + tasks + "\nQuery: " + question))
        );
    }

    private LocalDateTime getLastDayOfMonth(LocalDateTime date) {
        return date.withDayOfMonth(date.getMonth().maxLength());
    }
}
