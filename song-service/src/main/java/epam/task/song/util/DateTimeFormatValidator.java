package epam.task.song.util;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class DateTimeFormatValidator implements ConstraintValidator<DateTimeFormat, String> {
    private String pattern;
    @Override
    public void initialize(DateTimeFormat constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        this.pattern = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        DateFormat sdf = new SimpleDateFormat(pattern);
        sdf.setLenient(false);
        try {
            sdf.parse(value);
        } catch (ParseException e) {
            return false;
        }
        return true;
    }
}
