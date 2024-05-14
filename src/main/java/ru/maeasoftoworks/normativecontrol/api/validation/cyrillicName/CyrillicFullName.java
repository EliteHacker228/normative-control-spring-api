package ru.maeasoftoworks.normativecontrol.api.validation.cyrillicName;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = CyrillicFullNameValidator.class)
@Target({ TYPE, FIELD, ANNOTATION_TYPE })
@Retention(RUNTIME)
@Documented
public @interface CyrillicFullName {
    String message() default "Your name should be an valid cyrillic name";
    Class <?> [] groups() default {};
    Class <? extends Payload> [] payload() default {};
}
