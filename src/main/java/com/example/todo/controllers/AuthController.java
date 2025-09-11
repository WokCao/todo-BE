package com.example.todo.controllers;

import com.example.todo.DTOs.UserDTO;
import com.example.todo.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserDTO userDTO) {
        boolean result = authService.register(userDTO);
        if (result) {
            return ResponseEntity.ok().body("User registered successfully");
        } else {
            return ResponseEntity.badRequest().body("User registration failed");
        }
    }
}
