package com.example.todo.services;

import com.example.todo.DTOs.UpdateUserDTO;
import com.example.todo.models.UserModel;
import com.example.todo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public Optional<UserModel> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        if (Objects.equals(email, null)) return Optional.empty();

        return userRepository.findByEmail(email);
    }

    public UserModel updateUser(UpdateUserDTO updateUserDTO) {
        Optional<UserModel> userOpt = getCurrentUser();
        if (userOpt.isEmpty()) throw new RuntimeException("User not found");

        UserModel user = userOpt.get();

        if (updateUserDTO.getFullname() != null && !updateUserDTO.getFullname().isBlank()) {
            user.setFullname(updateUserDTO.getFullname());
        }

        if (updateUserDTO.getPassword() != null && !updateUserDTO.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(updateUserDTO.getPassword()));
        }

        return userRepository.save(user);
    }
}
