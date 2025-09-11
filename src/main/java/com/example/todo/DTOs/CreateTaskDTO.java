package com.example.todo.DTOs;

import com.example.todo.enums.PRIORITY;
import com.example.todo.enums.STATUS;
import com.example.todo.validations.FutureOrPresentDate;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.util.Date;

@Data
public class CreateTaskDTO {
    @NotBlank(message = "Title cannot be empty")
    private String title;

    @Length(max = 1000, message = "Description cannot be longer than 1000 characters")
    private String description;

    private PRIORITY priority;
    private STATUS status;

    @FutureOrPresentDate()
    private Date dueDate;
}
