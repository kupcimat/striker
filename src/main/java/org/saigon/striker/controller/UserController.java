package org.saigon.striker.controller;

import org.saigon.striker.model.User;
import org.saigon.striker.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriTemplate;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

import static org.apache.commons.lang3.Validate.notNull;
import static org.saigon.striker.model.UserModelsKt.toEntity;
import static org.saigon.striker.model.UserModelsKt.toUser;

@RestController
public class UserController {

    public static final String USERS_URI_TEMPLATE = "/admin/user";
    public static final String USER_URI_TEMPLATE = USERS_URI_TEMPLATE + "/{userId}";

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = notNull(userService);
    }

    @PostMapping(USERS_URI_TEMPLATE)
    public Mono<ResponseEntity<User>> createUser(@Valid @RequestBody User user) {
        return userService.createUser(toEntity(user))
                .map(userEntity -> ResponseEntity
                        .created(new UriTemplate(USER_URI_TEMPLATE).expand(userEntity.getId()))
                        .body(toUser(userEntity).withoutPassword()));
    }

    @GetMapping(USER_URI_TEMPLATE)
    public Mono<ResponseEntity<User>> getUser(@PathVariable String userId) {
        return userService.getUser(userId)
                .map(userEntity -> ResponseEntity.ok(toUser(userEntity).withoutPassword()))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping(USER_URI_TEMPLATE)
    public Mono<ResponseEntity<?>> deleteUser(@PathVariable String userId) {
        return userService.deleteUser(userId)
                .thenReturn(ResponseEntity.noContent().build());
    }
}
