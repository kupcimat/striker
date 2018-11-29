package org.saigon.striker.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.saigon.striker.model.UserEntity;
import org.saigon.striker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@WebFluxTest(UserController.class)
@WithMockUser
public class UserControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private UserService userService;

    @Test
    public void getUser() {
        when(userService.getUser("1"))
                .thenReturn(Mono.just(new UserEntity("1", "username", "password")));

        webTestClient.get().uri("/admin/user/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("user.username").isEqualTo("username");
    }
}
