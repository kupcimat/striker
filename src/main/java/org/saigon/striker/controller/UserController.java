package org.saigon.striker.controller;

import org.saigon.striker.model.User;
import org.saigon.striker.model.UserEntity;
import org.saigon.striker.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

import static org.apache.commons.lang3.Validate.notNull;

// TODO switch to reactive controller
@RestController
@RequestMapping(UserController.USER_URI)
public class UserController {

    public static final String USER_URI = "/admin/user";

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = notNull(userService);
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        var userEntity = userService.createUser(UserEntity.fromUser(user));

        return ResponseEntity.created(URI.create(USER_URI + userEntity.getId()))
                .body(userEntity.toUser());
    }

    @GetMapping("/{userId}")
    public ResponseEntity<User> getUser(@PathVariable long userId) {
        var userEntity = userService.getUser(userId);

        return ResponseEntity.ok(userEntity.toUser());
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable long userId) {
        userService.deleteUser(userId);

        return ResponseEntity.noContent().build();
    }
}
