package epam.task.song.controller;

import epam.task.song.exception.EntityAlreadyExistsException;
import epam.task.song.reqres.ErrorMessage;
import epam.task.song.reqres.ErrorMessageDetails;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.List;


@RestControllerAdvice
public class SongRestControllerAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Object> handleEntityNotFoundException(EntityNotFoundException ex) {
        return createErrorMessage(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EntityAlreadyExistsException.class)
    public ResponseEntity<Object> handleEntityAlreadyExistsException(EntityAlreadyExistsException ex) {
        return createErrorMessage(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex) {
        return createErrorMessage(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        BindingResult bindingResult = ex.getBindingResult();
        List<ObjectError> errors = bindingResult.getAllErrors();

        ErrorMessageDetails.ErrorMessageDetailsBuilder<?, ?> builder = ErrorMessageDetails.builder();
        builder.errorMessage("invalid request body parameters");
        builder.errorCode(String.valueOf(HttpStatus.BAD_REQUEST.value()));

        final HashMap<String, String> errorMap = new HashMap<>();

        errors.forEach(error -> {
            if (error instanceof FieldError fieldError){
                //Object rejectedValue = fieldError.getRejectedValue();
                errorMap.put(fieldError.getField(), error.getDefaultMessage());
            }
        });
        builder.details(errorMap);
        return ResponseEntity.badRequest().body(builder.build());
    }

    @Override
    protected ResponseEntity<Object> handleHandlerMethodValidationException(HandlerMethodValidationException ex,
                                                                            HttpHeaders headers,
                                                                            HttpStatusCode status,
                                                                            WebRequest request) {
        return createErrorMessage(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    private static ResponseEntity<Object> createErrorMessage(String ex, HttpStatus status) {
        ErrorMessage errorMessage = ErrorMessage.builder()
                .errorCode(String.valueOf(status.value()))
                .errorMessage(ex)
                .build();

        return new ResponseEntity<>(errorMessage, status);
    }
}
