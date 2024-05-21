package ru.maeasoftoworks.normativecontrol.api.validation.lengthinbytes;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import ru.maeasoftoworks.normativecontrol.api.validation.docxdocument.DocxDocumentValidator;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = LengthInBytesValidator.class)
@Target({ TYPE, FIELD, ANNOTATION_TYPE })
@Retention(RUNTIME)
public @interface LengthInBytes {
    String message() default "";
    long min() default Long.MIN_VALUE;
    long max() default Long.MAX_VALUE;
    Class <?> [] groups() default {};
    Class <? extends Payload> [] payload() default {};
}
