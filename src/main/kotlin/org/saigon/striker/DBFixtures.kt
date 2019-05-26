package org.saigon.striker

import org.saigon.striker.model.UserEntity
import org.saigon.striker.service.UserService
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Profile
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import java.time.Duration

@ConfigurationProperties("fixtures")
data class FixturesProperties(
    val username: String,
    val password: String
)

@Component
@Profile(Profiles.NOT_HEROKU)
class DBFixtures(val properties: FixturesProperties, val userService: UserService) {

    @EventListener(ApplicationReadyEvent::class)
    fun createFixtures() {
        userService.getUserByUsername(properties.username)
            .switchIfEmpty(userService.createUser(UserEntity(properties.username, properties.password)))
            .block(Duration.ofSeconds(60))
    }
}
