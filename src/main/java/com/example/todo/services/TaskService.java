package com.example.todo.services;

import com.example.todo.DTOs.CreateTaskDTO;
import com.example.todo.DTOs.PagedDataDTO;
import com.example.todo.enums.PRIORITY;
import com.example.todo.enums.SORTDIR;
import com.example.todo.enums.STATUS;
import com.example.todo.models.TaskModel;
import com.example.todo.models.UserModel;
import com.example.todo.repositories.TaskRepository;
import com.example.todo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
public class TaskService {
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private UserRepository userRepository;

    @Transactional
    public TaskModel createTask(CreateTaskDTO createTaskDTO) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        // Find the user
        UserModel userModel = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        TaskModel taskModel = new TaskModel();
        taskModel.setTitle(createTaskDTO.getTitle());
        taskModel.setDescription(createTaskDTO.getDescription());
        taskModel.setPriority(createTaskDTO.getPriority());
        taskModel.setStatus(createTaskDTO.getStatus());
        taskModel.setDueDate(createTaskDTO.getDueDate());

        taskModel.setUser(userModel);

        return taskRepository.save(taskModel);
    }

    private TaskModel checkUserAuthorization(Long id) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        // Find the user
        UserModel userModel = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        TaskModel taskModel = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (!taskModel.getUser().getId().equals(userModel.getId())) {
            throw new RuntimeException("User not authorized to update this task");
        }

        return taskModel;
    }

    @Transactional
    public TaskModel updateTask(Long id, CreateTaskDTO createTaskDTO) throws Exception {
        TaskModel taskModel = checkUserAuthorization(id);

        taskModel.setTitle(createTaskDTO.getTitle());
        taskModel.setDescription(createTaskDTO.getDescription());
        taskModel.setPriority(createTaskDTO.getPriority());
        taskModel.setStatus(createTaskDTO.getStatus());
        taskModel.setDueDate(createTaskDTO.getDueDate());
        return taskRepository.save(taskModel);
    }

    public void deleteTask(Long id) throws Exception {
        TaskModel taskModel = checkUserAuthorization(id);

        taskRepository.delete(taskModel);
    }

    public TaskModel getTask(Long id) throws Exception {
        return checkUserAuthorization(id);
    }

    public PagedDataDTO<TaskModel> getTasksWithPaginationAndSorting(int rawPage, int rawSize, String rawSortBy, SORTDIR rawSortDir, STATUS rawStatus, PRIORITY rawPriority, LocalDateTime fromDateTime, LocalDateTime toDateTime) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        // Find the user
        UserModel userModel = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        int page = Math.max(rawPage, 1);
        int size = (rawSize <= 0 || rawSize > 100) ? 10 : rawSize;
        String sortBy = validateSortBy(rawSortBy);
        SORTDIR sortDir = rawSortDir == null ? SORTDIR.ASC : rawSortDir;
        STATUS status = rawStatus == null ? STATUS.ALL : rawStatus;
        PRIORITY priority = rawPriority == null ? PRIORITY.ALL : rawPriority;

        Pageable pageable = PageRequest.of(page - 1, size, sortDir == SORTDIR.ASC ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending());
        Page<TaskModel> taskPage;

        taskPage = taskRepository.findTasksByUserEmailAndStatusAndPriorityBetween(userModel.getEmail(), status, priority, fromDateTime, toDateTime, pageable);

        return new PagedDataDTO<>(
                taskPage.getContent(),
                taskPage.getNumber(),
                taskPage.getSize(),
                taskPage.getTotalElements(),
                taskPage.getTotalPages(),
                taskPage.isFirst(),
                taskPage.isLast()
        );
    }

    private String validateSortBy(String sortBy) {
        if (sortBy == null || sortBy.trim().isEmpty()) {
            return "id";
        }

        // List of allowed sort fields - only allow sorting by safe columns
        Set<String> allowedSortFields = Set.of("id", "title", "priority", "status", "dueDate");

        return allowedSortFields.contains(sortBy) ? sortBy : "id";
    }

    @Scheduled(fixedRate = 60 * 60 * 1000)
    public void updateOverdueTasks() {
        LocalDateTime now = LocalDateTime.now();

        // Example: mark tasks as OVERDUE if past due date and still TODO or IN_PROGRESS
        List<TaskModel> tasks = taskRepository.findByStatusAndDueDateBefore(List.of(STATUS.TODO, STATUS.IN_PROGRESS), now);

        for (TaskModel task : tasks) {
            task.setStatus(STATUS.OVERDUE);
        }

        taskRepository.saveAll(tasks);
    }
}
