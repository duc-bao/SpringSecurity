package com.example.springsecurity.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.Period;

import java.time.LocalDate;
import java.time.chrono.Chronology;
import java.util.Objects;

public class DobValidator implements ConstraintValidator<DobContrains, LocalDate> {
    private int min;

    @Override
    public void initialize(DobContrains constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        min = constraintAnnotation.min();
    }

    @Override
    public boolean isValid(LocalDate localDate, ConstraintValidatorContext constraintValidatorContext) {
        if(Objects.isNull(localDate)){
            return true;
        }
        int age = Period.between(localDate, LocalDate.now()).getYears();

        return age  >= min;
    }
}
