package epam.task.song.controller;

import epam.task.song.reqres.ErrorMessage;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.List;


@RestControllerAdvice
public class SongRestControllerAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorMessage> handleEntityNotFoundException(EntityNotFoundException ex) {
        ErrorMessage errorMessage = ErrorMessage.builder()
                .errorCode(String.valueOf(HttpStatus.NOT_FOUND.value()))
                .errorMessage(ex.getMessage())
                .build();

        return new ResponseEntity<>(errorMessage, HttpStatus.NOT_FOUND);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        BindingResult bindingResult = ex.getBindingResult();
        List<ObjectError> errors = bindingResult.getAllErrors();

        ErrorMessage.ErrorMessageBuilder builder = ErrorMessage.builder();
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
}
