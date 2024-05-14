package ru.maeasoftoworks.normativecontrol.api.validation.cyrillicName;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;

public class CyrillicFullNameValidator implements ConstraintValidator<CyrillicFullName, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        String regexp = "^(?=.{1,40}$)[а-яёА-ЯЁ]+(?:[-' ][а-яёА-ЯЁ]+)*$";
        if (value == null)
            return false;

        String[] nameTokens = value.split(" ");
        String[] filteredNameTokens = Arrays.stream(nameTokens)
                .filter(nameToken -> nameToken.matches(regexp))
                .toArray(String[]::new);

        if(nameTokens.length != filteredNameTokens.length)
            return false;

        if(filteredNameTokens.length < 2)
            return false;

        return true;
    }
}
