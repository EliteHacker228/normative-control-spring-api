package ru.maeasoftoworks.normativecontrol.api.validation.universityEmailValidation;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = UniversityEmailValidator.class)
@Target({ TYPE, FIELD, ANNOTATION_TYPE })
@Retention(RUNTIME)
@Documented
public @interface UniversityEmail {
    String message() default "Your email is in wrong domain - must be in supported university domain";
    Class <?> [] groups() default {};
    Class <? extends Payload> [] payload() default {};
}
