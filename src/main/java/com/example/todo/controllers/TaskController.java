package com.example.todo.controllers;

import com.example.todo.DTOs.CreateTaskDTO;
import com.example.todo.models.TaskModel;
import com.example.todo.services.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
