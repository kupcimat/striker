package org.saigon.striker.config

import kotlinx.coroutines.runBlocking
import org.saigon.striker.Profiles
import org.saigon.striker.model.UserEntity
import org.saigon.striker.service.UserService
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@Profile(Profiles.NOT_HEROKU)
class FixturesConfig(val properties: FixturesProperties) {

    @Bean
    fun databaseInitializer(userService: UserService) = ApplicationRunner {
        runBlocking {
            if (userService.getUserByUsername(properties.username) == null) {
                userService.createUser(UserEntity(properties.username, properties.password))
            }
        }
    }
}
