package org.saigon.striker

import kotlinx.coroutines.runBlocking
import org.saigon.striker.model.UserEntity
import org.saigon.striker.service.UserService
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Profile
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@ConfigurationProperties("fixtures")
data class FixturesProperties(
    val username: String,
    val password: String
)

@Component
@Profile(Profiles.NOT_HEROKU)
class DBFixtures(val properties: FixturesProperties, val userService: UserService) {

    @EventListener(ApplicationReadyEvent::class)
    fun createFixtures(): Unit = runBlocking<Unit> {
        if (userService.getUserByUsername(properties.username) == null) {
            userService.createUser(UserEntity(properties.username, properties.password))
        }
    }
}
