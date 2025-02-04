package epam.task.song.util;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = YearValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Year {
    boolean longFormat() default false;//short(YY) version if false otherwise long - YYYY
    String message() default "invalid year format";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
