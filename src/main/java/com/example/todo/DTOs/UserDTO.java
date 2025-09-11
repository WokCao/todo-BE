package com.example.todo.DTOs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

@Data
@ToString
public class UserDTO {

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Invalid email address")
    private String email;

    @NotBlank(message = "Password cannot be empty")
    @Length(min = 8, message = "Password must be at least 8 characters long")
    private String password;

    @NotBlank(message = "Fullname cannot be empty")
    private String fullname;
}
