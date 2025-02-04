package epam.task.resource.controller;

import epam.task.resource.exception.FileFormatException;
import epam.task.resource.reqres.ErrorMessage;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class ResourceRestControllerAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<ErrorMessage> handleMultipartException(MultipartException ex) {
        return createErrorMessage(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(FileFormatException.class)
    public ResponseEntity<ErrorMessage> handleFileFormatException(FileFormatException ex) {
        return createErrorMessage(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorMessage> handleIllegalArgumentException(IllegalArgumentException ex) {
        return createErrorMessage(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorMessage> handleEntityNotFoundException(EntityNotFoundException ex) {
        return createErrorMessage(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    private static ResponseEntity<ErrorMessage> createErrorMessage(String ex, HttpStatus status) {
        ErrorMessage errorMessage = ErrorMessage.builder()
                .errorCode(String.valueOf(status.value()))
                .errorMessage(ex)
                .build();

        return new ResponseEntity<>(errorMessage, status);
    }
}
