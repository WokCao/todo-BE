package com.example.todo.controllers;

import com.example.todo.DTOs.CreateTaskDTO;
import com.example.todo.DTOs.PagedDataDTO;
import com.example.todo.DTOs.SuggestionDTO;
import com.example.todo.enums.PRIORITY;
import com.example.todo.enums.SORTDIR;
import com.example.todo.enums.STATUS;
import com.example.todo.models.TaskModel;
import com.example.todo.services.OpenRouterService;
import com.example.todo.services.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/tasks")
public class TaskController {
    @Autowired
    private TaskService taskService;
    @Autowired
    private OpenRouterService openRouterService;

    @PostMapping
    public ResponseEntity<?> createTask(@Valid @RequestBody CreateTaskDTO createTaskDTO) {
        try {
            TaskModel taskModel = taskService.createTask(createTaskDTO);
            return ResponseEntity.ok().body(taskModel);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getTasksWithPaginationAndSorting(
            @RequestParam(defaultValue = "1", required = false) int page,
            @RequestParam(defaultValue = "10", required = false) int size,
            @RequestParam(defaultValue = "id", required = false) String sortBy,
            @RequestParam(defaultValue = "ASC", required = false) SORTDIR sortDir,
            @RequestParam(defaultValue = "ALL", required = false) STATUS status,
            @RequestParam(defaultValue = "ALL", required = false) PRIORITY priority,
            @RequestParam(defaultValue = "", required = false) LocalDateTime fromDateTime,
            @RequestParam(defaultValue = "", required = false) LocalDateTime toDateTime) {
        try {
            PagedDataDTO<TaskModel> taskModels = taskService.getTasksWithPaginationAndSorting(page, size, sortBy, sortDir, status, priority, fromDateTime, toDateTime);
            taskService.updateOverdueTasks();
            return ResponseEntity.ok().body(taskModels);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTask(@PathVariable Long id, @Valid @RequestBody CreateTaskDTO createTaskDTO) {
        try {
            TaskModel taskModel = taskService.updateTask(id, createTaskDTO);
            return ResponseEntity.ok().body(taskModel);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id) {
        try {
            taskService.deleteTask(id);
            return ResponseEntity.ok().body("Task deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTask(@PathVariable Long id) {
        try {
            TaskModel taskModel = taskService.getTask(id);
            return ResponseEntity.ok().body(taskModel);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/suggestion")
    public ResponseEntity<?> getSuggestion(@RequestBody String question) {
        try {
            SuggestionDTO result = openRouterService.askQuestionWithRateLimit(question);
            return ResponseEntity.ok().body(Objects.requireNonNullElse(result, "Cannot answer this question"));
        } catch (Exception e) {
            if (Objects.equals(e.getMessage(), "Rate limit exceeded")) return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("Rate limit exceeded");
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
