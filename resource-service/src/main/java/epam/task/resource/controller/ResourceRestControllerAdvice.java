package epam.task.resource.controller;

import epam.task.resource.exception.FileFormatException;
import epam.task.resource.exception.ValidationException;
import epam.task.resource.reqres.DetailedErrorMessage;
import epam.task.resource.reqres.ErrorMessage;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ResourceRestControllerAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<Object> handleMultipartException(MultipartException e) {
        return new ResponseEntity<>(createErrorMessage(e.getMessage(), HttpStatus.BAD_REQUEST.value()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(FileFormatException.class)
    public ResponseEntity<Object> handleFileFormatException(FileFormatException e) {
        return new ResponseEntity<>(createErrorMessage(e.getMessage(), HttpStatus.BAD_REQUEST.value()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Object> handleValidationException(ValidationException e) {
        return new ResponseEntity<>(createErrorMessage(e.getMessage(), HttpStatus.BAD_REQUEST.value(), e.getDetails()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Object> handleEntityNotFoundException(EntityNotFoundException e) {
        return new ResponseEntity<>(createErrorMessage(e.getMessage(), HttpStatus.NOT_FOUND.value()),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Object> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        Map<String, String> details = Map.of(e.getName(), "Invalid value '"+e.getValue()+"'");
        return new ResponseEntity<>(createErrorMessage("Invalid parameter", HttpStatus.BAD_REQUEST.value(), details),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RestClientException.class)
    public ResponseEntity<Object> handleRestClientException(RestClientException e) {
        return new ResponseEntity<>(createErrorMessage("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR.value()),
                HttpStatus.INTERNAL_SERVER_ERROR);
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

        return new ResponseEntity<>(createErrorMessage(ex.getReason(), HttpStatus.BAD_REQUEST.value(), details),
                HttpStatus.BAD_REQUEST);
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
