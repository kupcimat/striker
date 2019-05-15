package org.saigon.striker.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.saigon.striker.model.User;
import org.saigon.striker.model.UserEntity;
import org.saigon.striker.service.UserService;
import org.saigon.striker.service.UsernameAlreadyExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

@RunWith(SpringRunner.class)
@WebFluxTest(UserController.class)
@WithMockUser
// TODO security config not applied!
public class UserControllerTest {

    private static final String ID = "userId";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final UserEntity USER_ENTITY = new UserEntity(ID, USERNAME, PASSWORD);

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private UserService userService;

    @Test
    public void createUser() {
        when(userService.createUser(any())).thenReturn(Mono.just(USER_ENTITY));

        webTestClient.mutateWith(csrf()).post().uri("/admin/user")
                .syncBody(new User(USERNAME, PASSWORD))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(User.class).isEqualTo(new User(USERNAME, null));
    }

    @Test
    public void createUserAlreadyExists() {
        when(userService.createUser(any())).thenReturn(Mono.error(new UsernameAlreadyExistsException(USERNAME)));

        webTestClient.mutateWith(csrf()).post().uri("/admin/user")
                .syncBody(new User(USERNAME, PASSWORD))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody().jsonPath("message").value(message -> assertThat(message).isEqualTo("Username 'username' already exists"), String.class);
    }

    @Test
    public void createUserInvalid() {
        // short username and password
        webTestClient.mutateWith(csrf()).post().uri("/admin/user")
                .syncBody(new User("a", "b"))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody().jsonPath("message").value(message -> assertThat(message).contains("Validation failed"), String.class);

        // missing username and password
        webTestClient.mutateWith(csrf()).post().uri("/admin/user")
                .syncBody(new User(null, null))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody().jsonPath("message").value(message -> assertThat(message).contains("Validation failed"), String.class);
    }

    @Test
    public void getUser() {
        when(userService.getUser(ID)).thenReturn(Mono.just(USER_ENTITY));

        webTestClient.get().uri("/admin/user/" + ID)
                .exchange()
                .expectStatus().isOk()
                .expectBody(User.class).isEqualTo(new User(USERNAME, null));
    }

    @Test
    public void getUserNotFound() {
        when(userService.getUser("invalidUserId")).thenReturn(Mono.empty());

        webTestClient.get().uri("/admin/user/invalidUserId")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody().isEmpty();
    }

    @Test
    public void deleteUser() {
        when(userService.deleteUser(ID)).thenReturn(Mono.empty());

        webTestClient.mutateWith(csrf()).delete().uri("/admin/user/" + ID)
                .exchange()
                .expectStatus().isNoContent()
                .expectBody().isEmpty();
    }
}
