package org.saigon.striker;

import org.saigon.striker.model.UserEntity;
import org.saigon.striker.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.Duration;

import static org.apache.commons.lang3.Validate.notEmpty;
import static org.apache.commons.lang3.Validate.notNull;

@Component
@Profile("!heroku")
public class MongoDBFixtures implements CommandLineRunner {

    private static final Duration MONGODB_TIMEOUT = Duration.ofSeconds(60);

    private final String devUsername;
    private final String devPassword;
    private final UserService userService;

    public MongoDBFixtures(@Value("${dev.username}") String devUsername,
                           @Value("${dev.password}") String devPassword,
                           UserService userService) {
        this.devUsername = notEmpty(devUsername);
        this.devPassword = notEmpty(devPassword);
        this.userService = notNull(userService);
    }

    @Override
    public void run(String... args) throws Exception {
        userService.getUserByUsername(devUsername)
                .switchIfEmpty(userService.createUser(UserEntity.of(devUsername, devPassword)))
                .block(MONGODB_TIMEOUT);
    }
}
