package ru.maeasoftoworks.normativecontrol.api.validation.docxdocument;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;
import ru.maeasoftoworks.normativecontrol.api.validation.universityEmail.UniversityEmail;

public class DocxDocumentValidator implements ConstraintValidator<DocxDocument, MultipartFile>  {
    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext constraintValidatorContext) {
        return file != null && file.getContentType() != null && file.getContentType().equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
    }
}
