package org.saigon.striker.controller

import org.saigon.striker.model.User
import org.saigon.striker.model.toEntity
import org.saigon.striker.model.toUserWithoutPassword
import org.saigon.striker.service.UserService
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.function.server.ServerResponse.*

class UserHandler(private val userService: UserService) {

    suspend fun createUser(request: ServerRequest): ServerResponse {
        val user = request.awaitBody<User>()
        val userEntity = userService.createUser(user.toEntity())
        return created(UriTemplates.expandUser(userEntity.id)).bodyValueAndAwait(userEntity.toUserWithoutPassword())
    }

    suspend fun getUser(request: ServerRequest): ServerResponse {
        val userId = request.pathVariable("userId")
        val userEntity = userService.getUser(userId) ?: return notFound().buildAndAwait()
        return ok().bodyValueAndAwait(userEntity.toUserWithoutPassword())
    }

    suspend fun deleteUser(request: ServerRequest): ServerResponse {
        val userId = request.pathVariable("userId")
        userService.deleteUser(userId)
        return noContent().buildAndAwait()
    }
}
