package com.example.todo.controllers;

import com.example.todo.DTOs.LoginRequestDTO;
import com.example.todo.DTOs.UserDTO;
import com.example.todo.services.AuthService;
import com.example.todo.services.JwtService;
import com.example.todo.services.MailService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    @Autowired
    private AuthService authService;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private MailService mailService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserDTO userDTO) {
        boolean result = authService.register(userDTO);
        if (result) {
            mailService.sendWelcomeMail(userDTO.getEmail());
            return ResponseEntity.ok().body("User registered successfully");
        } else {
            return ResponseEntity.badRequest().body("User registration failed");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        boolean isAuthenticated = authService.authenticate(loginRequestDTO.getEmail(), loginRequestDTO.getPassword());

        if (isAuthenticated) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            return ResponseEntity.ok().body(jwtService.generateToken(authentication));
        } else {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
    }
}
