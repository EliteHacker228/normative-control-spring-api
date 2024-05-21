package ru.maeasoftoworks.normativecontrol.api.validation.lengthinbytes;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

public class LengthInBytesValidator implements ConstraintValidator<LengthInBytes, MultipartFile> {

    private long min;
    private long max;

    @Override
    public void initialize(LengthInBytes constraintAnnotation) {
        this.min = constraintAnnotation.min();
        this.max = constraintAnnotation.max();
    }

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext constraintValidatorContext) {
        return file.getSize() <= max && file.getSize() >= min;
    }
}
