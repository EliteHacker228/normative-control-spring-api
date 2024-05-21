package ru.maeasoftoworks.normativecontrol.api.validation.docxdocument;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import ru.maeasoftoworks.normativecontrol.api.validation.universityEmail.UniversityEmailValidator;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = DocxDocumentValidator.class)
@Target({ TYPE, FIELD, ANNOTATION_TYPE })
@Retention(RUNTIME)
public @interface DocxDocument {
    String message() default "";
    Class <?> [] groups() default {};
    Class <? extends Payload> [] payload() default {};
}
