package com.example.todo.DTOs;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class SuggestionDTO {
    private List<Suggestion> schedule;

    @Data
    public static class Suggestion {
        private Long taskId;
        private String title;
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
        private LocalDateTime suggestedStart;
        private int durationMinutes;
        private String summary;
    }
}
