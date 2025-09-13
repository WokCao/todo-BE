package com.example.todo.validations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDateTime;

public class FutureOrPresentDateValidator implements ConstraintValidator<FutureOrPresentDate, LocalDateTime> {
    private int minutesOffset;

    @Override
    public void initialize(FutureOrPresentDate constraintAnnotation) {
        this.minutesOffset = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(LocalDateTime dueDate, ConstraintValidatorContext context) {
        if (dueDate == null) {
            return true;
        }

        LocalDateTime now = LocalDateTime.now();

        LocalDateTime minimumAllowedDate = now.plusMinutes(minutesOffset);

        return !dueDate.isBefore(minimumAllowedDate);
    }
}
