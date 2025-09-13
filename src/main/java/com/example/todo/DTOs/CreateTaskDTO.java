package com.example.todo.DTOs;

import com.example.todo.enums.PRIORITY;
import com.example.todo.enums.STATUS;
import com.example.todo.validations.FutureOrPresentDate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

@Data
public class CreateTaskDTO {
    @NotBlank(message = "Title cannot be empty")
    private String title;

    @Length(max = 1000, message = "Description cannot be longer than 1000 characters")
    private String description;

    @NotNull(message = "Priority cannot be empty")
    private PRIORITY priority;

    @NotNull(message = "Status cannot be empty")
    private STATUS status;

    @FutureOrPresentDate(value = 60)
    private LocalDateTime dueDate;
}
