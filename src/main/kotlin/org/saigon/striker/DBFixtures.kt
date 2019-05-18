package org.saigon.striker

import org.saigon.striker.model.UserEntity
import org.saigon.striker.service.UserService
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.annotation.Profile
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import java.time.Duration

@Component
@Profile("!heroku")
class DBFixtures(
    @Value("\${dev.username}") val devUsername: String,
    @Value("\${dev.password}") val devPassword: String,
    val userService: UserService
) {

    @EventListener(ApplicationReadyEvent::class)
    fun createFixtures() {
        userService.getUserByUsername(devUsername)
            .switchIfEmpty(userService.createUser(UserEntity(devUsername, devPassword)))
            .block(Duration.ofSeconds(60))
    }
}
