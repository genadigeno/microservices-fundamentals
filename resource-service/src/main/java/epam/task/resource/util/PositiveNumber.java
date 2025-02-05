package epam.task.resource.util;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PositiveNumberValidator.class)
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PositiveNumber {
    String message() default "ID must be a number";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
