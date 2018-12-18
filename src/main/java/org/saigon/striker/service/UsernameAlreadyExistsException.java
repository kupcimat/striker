package org.saigon.striker.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static java.lang.String.format;

public class UsernameAlreadyExistsException extends ResponseStatusException {

    public UsernameAlreadyExistsException(String username) {
        super(HttpStatus.BAD_REQUEST, format("Username '%s' already exists", username));
    }
}
