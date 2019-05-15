package org.saigon.striker.config;

import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.Map;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

@Component
public class DefaultErrorResponse extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request, boolean includeStackTrace) {
        var errorResponse = super.getErrorAttributes(request, includeStackTrace);
        var error = getError(request);

        if (error instanceof BindingResult) {
            errorResponse.remove("errors");
            errorResponse.put("message", createValidationErrorMessage((BindingResult) error));
        }

        return errorResponse;
    }

    private String createValidationErrorMessage(BindingResult validationResult) {
        var errorMessages = Stream.concat(
                validationResult.getFieldErrors().stream().map(this::createFieldErrorMessage),
                validationResult.getGlobalErrors().stream().map(this::createGlobalErrorMessage)
        ).collect(toList());

        return String.join(". ", errorMessages);
    }

    private String createFieldErrorMessage(FieldError error) {
        return format("Error in object '%s' on field '%s': %s",
                error.getObjectName(), error.getField(), error.getDefaultMessage());
    }

    private String createGlobalErrorMessage(ObjectError error) {
        return format("Error in object '%s': %s", error.getObjectName(), error.getDefaultMessage());
    }
}
