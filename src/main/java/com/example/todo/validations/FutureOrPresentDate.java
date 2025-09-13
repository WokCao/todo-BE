package com.example.todo.validations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = FutureOrPresentDateValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface FutureOrPresentDate {
    String message() default "Due date must be in the future or present";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    int value() default 20;
}
