package com.example.todo.DTOs;

import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class UpdateUserDTO {
    @Pattern(
            regexp = "^[A-Za-z ]+$",
            message = "Fullname can only contain letters and spaces"
    )
    private String fullname;
    private String password;
}
