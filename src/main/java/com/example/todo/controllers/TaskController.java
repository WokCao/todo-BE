package com.example.todo.controllers;

import com.example.todo.DTOs.CreateTaskDTO;
import com.example.todo.models.TaskModel;
import com.example.todo.services.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/tasks")
public class TaskController {
    @Autowired
    private TaskService taskService;

    @PostMapping("")
    public ResponseEntity<?> createTask(@Valid @RequestBody CreateTaskDTO createTaskDTO) {
        try {
            TaskModel taskModel = taskService.createTask(createTaskDTO);
            return ResponseEntity.ok().body(taskModel);
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
}
