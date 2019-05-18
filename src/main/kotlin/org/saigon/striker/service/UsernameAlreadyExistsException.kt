package org.saigon.striker.service

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

class UsernameAlreadyExistsException(username: String) :
    ResponseStatusException(HttpStatus.BAD_REQUEST, "Username '$username' already exists")
