package com.sirotenkod.test.usersmanage.exception.handler;

import com.sirotenkod.test.usersmanage.exception.NotFoundException;
import com.sirotenkod.test.usersmanage.response.ErrorResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Objects;
import java.util.stream.Collectors;

@RestControllerAdvice
public class CustomResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        String fields = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getField)
                .collect(Collectors.joining(", "));

        ErrorResponse errorResponse = new ErrorResponse(status, "Invalid fields: " + fields);

        return handleExceptionInternal(ex, errorResponse, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
        if (Objects.isNull(body)) {
            if (!Objects.isNull(ex.getMessage())) {
                body = new ErrorResponse(status, ex.getMessage());
            } else {
                body = new ErrorResponse(status);
            }
        }

        return super.handleExceptionInternal(ex, body, headers, status, request);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Object> handleNotFound(Exception ex, WebRequest request) {
        HttpHeaders headers = new HttpHeaders();

        return handleExceptionInternal(ex, null, headers, HttpStatus.NOT_FOUND, request);
    }
}
