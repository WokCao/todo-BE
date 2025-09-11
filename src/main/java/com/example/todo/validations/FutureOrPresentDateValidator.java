package com.example.todo.validations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Date;

public class FutureOrPresentDateValidator implements ConstraintValidator<FutureOrPresentDate, Date> {
    @Override
    public boolean isValid(Date dueDate, ConstraintValidatorContext context) {
        if (dueDate == null) {
            return true;
        }

        Date currentDate = new Date();
        return !dueDate.before(currentDate);
    }
}
