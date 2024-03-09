package ru.maeasoftoworks.normativecontrol.api.validation.universityEmailValidation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UniversityEmailValidator implements ConstraintValidator<UniversityEmail, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value.endsWith("urfu.me") || value.endsWith("at.urfu.ru") || value.endsWith("urfu.ru");
    }
}
