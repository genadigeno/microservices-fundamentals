package epam.task.song.util;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class YearValidator implements ConstraintValidator<Year, String> {
    private boolean longFormat;
    @Override
    public void initialize(Year constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        this.longFormat = constraintAnnotation.longFormat();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (longFormat){
            return value.matches("\\d{4}");
        }
        return value.matches("\\d{2}");
    }
}
