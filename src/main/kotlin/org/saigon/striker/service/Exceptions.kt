package org.saigon.striker.service

import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Mono

class ValidationException(message: String) :
    ResponseStatusException(HttpStatus.BAD_REQUEST, message)

class UsernameAlreadyExistsException(username: String) :
    ResponseStatusException(HttpStatus.BAD_REQUEST, "Username '$username' already exists")

class AgodaApiException(status: HttpStatus) :
    ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Agoda API error: $status")

object Exceptions {
    fun handleAgodaError(clientResponse: ClientResponse): Mono<AgodaApiException> =
        Mono.just(AgodaApiException(clientResponse.statusCode()))
}
