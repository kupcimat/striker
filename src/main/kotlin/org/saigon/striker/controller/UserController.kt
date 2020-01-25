package org.saigon.striker.controller

import org.saigon.striker.model.User
import org.saigon.striker.model.toEntity
import org.saigon.striker.model.toUserWithoutPassword
import org.saigon.striker.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class UserController(val userService: UserService) {

    @PostMapping(UriTemplates.USERS)
    suspend fun createUser(@RequestBody user: User): ResponseEntity<User> {
        val userEntity = userService.createUser(user.toEntity())
        return ResponseEntity.created(UriTemplates.expandUser(userEntity.id))
            .body(userEntity.toUserWithoutPassword())
    }

    @GetMapping(UriTemplates.USER)
    suspend fun getUser(@PathVariable userId: String): ResponseEntity<User> {
        val userEntity = userService.getUser(userId) ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(userEntity.toUserWithoutPassword())
    }

    @DeleteMapping(UriTemplates.USER)
    suspend fun deleteUser(@PathVariable userId: String): ResponseEntity<Any> {
        userService.deleteUser(userId)
        return ResponseEntity.noContent().build()
    }
}
