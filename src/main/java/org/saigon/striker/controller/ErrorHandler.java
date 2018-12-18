package org.saigon.striker.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ResponseStatusException;

import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handle(WebExchangeBindException exception) {
        var errorMessages = Stream.concat(
                exception.getFieldErrors().stream().map(this::createFieldErrorMessage),
                exception.getGlobalErrors().stream().map(this::createGlobalErrorMessage)
        ).collect(toList());

        return ResponseEntity.badRequest()
                .body(new ErrorResponse(errorMessages));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handle(ResponseStatusException exception) {
        return ResponseEntity.status(exception.getStatus())
                .body(new ErrorResponse(exception.getReason()));
    }

    private String createFieldErrorMessage(FieldError error) {
        return format("Error in object '%s' on field '%s': %s",
                error.getObjectName(), error.getField(), error.getDefaultMessage());
    }

    private String createGlobalErrorMessage(ObjectError error) {
        return format("Error in object '%s': %s", error.getObjectName(), error.getDefaultMessage());
    }
}
