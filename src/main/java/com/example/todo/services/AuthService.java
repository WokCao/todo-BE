package com.example.todo.services;

import com.example.todo.DTOs.UserDTO;
import com.example.todo.models.UserModel;
import com.example.todo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;

    public boolean register(UserDTO userDTO) {
        try {
            Optional<UserModel> userModelOptional = userRepository.findByEmail(userDTO.getEmail());
            if (userModelOptional.isPresent()) {
                return false;
            }

            String hashedPassword = passwordEncoder.encode(userDTO.getPassword());
            UserModel userModel = new UserModel();
            userModel.setEmail(userDTO.getEmail());
            userModel.setPassword(hashedPassword);
            userModel.setFullname(userDTO.getFullname());
            userRepository.save(userModel);

            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public boolean authenticate(String email, String password) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
