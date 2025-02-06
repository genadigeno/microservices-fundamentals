package epam.task.song.controller;

import epam.task.song.exception.EntityAlreadyExistsException;
import epam.task.song.exception.IllegalParameterException;
import epam.task.song.reqres.ErrorMessage;
import epam.task.song.reqres.DetailedErrorMessage;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestControllerAdvice
public class SongRestControllerAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Object> handleEntityNotFoundException(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                createErrorMessage(ex.getMessage(), HttpStatus.NOT_FOUND.value())
        );
    }

    @ExceptionHandler(EntityAlreadyExistsException.class)
    public ResponseEntity<Object> handleEntityAlreadyExistsException(EntityAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                createErrorMessage(ex.getMessage(), HttpStatus.CONFLICT.value())
        );
    }

    @ExceptionHandler(IllegalParameterException.class)
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalParameterException ex) {
        return ResponseEntity.badRequest().body(
                createErrorMessage(ex.getMessage(), HttpStatus.BAD_REQUEST.value(), ex.getDetails())
        );
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        BindingResult bindingResult = ex.getBindingResult();
        List<ObjectError> errors = bindingResult.getAllErrors();

        final HashMap<String, String> details = new HashMap<>();
        errors.forEach(error -> {
            if (error instanceof FieldError fieldError){
                //Object rejectedValue = fieldError.getRejectedValue();
                details.put(fieldError.getField(), error.getDefaultMessage());
            }
        });

        return ResponseEntity.badRequest().body(createErrorMessage(
                "invalid request body parameters", HttpStatus.BAD_REQUEST.value(), details
        ));
    }

    @Override
    protected ResponseEntity<Object> handleHandlerMethodValidationException(HandlerMethodValidationException ex,
                                                                            HttpHeaders headers,
                                                                            HttpStatusCode status,
                                                                            WebRequest request) {
        final Map<String, String> details = new HashMap<>();
        List<ParameterValidationResult> validationResults = ex.getParameterValidationResults();
        validationResults.stream().findFirst().ifPresent(result -> {
            result.getResolvableErrors().stream().findFirst().ifPresent(error -> {
                String parameterName = result.getMethodParameter().getParameterName();
                details.put(parameterName, error.getDefaultMessage());
            });
        });

        return ResponseEntity.badRequest().body(
                createErrorMessage(ex.getReason(), HttpStatus.BAD_REQUEST.value(), details)
        );
    }

    private static ErrorMessage createErrorMessage(String errorMessage, int errorCode) {
        return ErrorMessage.builder()
                .errorCode(String.valueOf(errorCode))
                .errorMessage(errorMessage)
                .build();
    }

    private static ErrorMessage createErrorMessage(String errorMessage, int errorCode, Map<?, ?> details) {
        return DetailedErrorMessage.builder()
                .errorCode(String.valueOf(errorCode))
                .errorMessage(errorMessage)
                .details(details)
                .build();
    }
}
