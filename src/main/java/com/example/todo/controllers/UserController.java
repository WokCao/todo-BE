package com.example.todo.controllers;

import com.example.todo.DTOs.UpdateUserDTO;
import com.example.todo.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        return ResponseEntity.ok().body(userService.getCurrentUser());
    }

    @PatchMapping("/me")
    public ResponseEntity<?> updateUser(@Valid @RequestBody UpdateUserDTO updateUserDTO) {
        return ResponseEntity.ok().body(userService.updateUser(updateUserDTO));
    }
}
