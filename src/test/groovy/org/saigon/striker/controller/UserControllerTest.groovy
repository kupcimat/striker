package org.saigon.striker.controller

import org.saigon.striker.model.User
import org.saigon.striker.model.UserEntity
import org.saigon.striker.service.UserService
import org.saigon.striker.service.UsernameAlreadyExistsException
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.http.HttpStatus
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono
import spock.lang.Specification
import spock.lang.Unroll

import static org.saigon.striker.TestUtils.jsonEquals
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockUser

@WebFluxTest(UserController.class)
class UserControllerTest extends Specification {

    static final String USER_ID = "userId"
    static final String USERNAME = "username"
    static final String PASSWORD = "password"
    static final UserEntity USER_ENTITY = new UserEntity(USER_ID, USERNAME, PASSWORD)

    @Autowired
    WebTestClient webTestClient

    @SpringBean
    UserService userService = Stub()

    @Unroll
    def "POST user (#expectedJson)"() {
        given:
        userService.createUser(_ as UserEntity) >> mockedUser

        expect:
        api().post().uri("/admin/user")
                .syncBody(inputUser)
                .exchange()
                .expectStatus().isEqualTo(expectedStatus)
                .expectBody(String).value(jsonEquals(expectedJson))

        where:
        inputUser                    | expectedStatus         | expectedJson
        new User(USERNAME, PASSWORD) | HttpStatus.CREATED     | "user-200-ok.json"
        new User(USERNAME, PASSWORD) | HttpStatus.BAD_REQUEST | "user-400-already-exists.json"
        new User(null, PASSWORD)     | HttpStatus.BAD_REQUEST | "user-400-null-username.json"
        new User("a", PASSWORD)      | HttpStatus.BAD_REQUEST | "user-400-short-username.json"
        new User(USERNAME, null)     | HttpStatus.BAD_REQUEST | "user-400-null-password.json"
        new User(USERNAME, "a")      | HttpStatus.BAD_REQUEST | "user-400-short-password.json"

        mockedUser << [
                Mono.just(USER_ENTITY),
                Mono.error(new UsernameAlreadyExistsException(USERNAME)),
                Mono.just(USER_ENTITY),
                Mono.just(USER_ENTITY),
                Mono.just(USER_ENTITY),
                Mono.just(USER_ENTITY)
        ]
    }

    @Unroll
    def "GET user (#expectedJson)"() {
        given:
        userService.getUser(USER_ID) >> mockedUser

        expect:
        api().get().uri("/admin/user/{userId}", USER_ID)
                .exchange()
                .expectStatus().isEqualTo(expectedStatus)
                .expectBody(String).value(jsonEquals(expectedJson))

        where:
        mockedUser             | expectedStatus       | expectedJson
        Mono.just(USER_ENTITY) | HttpStatus.OK        | "user-200-ok.json"
        Mono.empty()           | HttpStatus.NOT_FOUND | "empty-response.json"
    }

    @Unroll
    def "DELETE user"() {
        given:
        userService.deleteUser(USER_ID) >> Mono.empty()

        expect:
        api().delete().uri("/admin/user/{userId}", USER_ID)
                .exchange()
                .expectStatus().isNoContent()
                .expectBody().isEmpty()
    }

    WebTestClient api() {
        return webTestClient
                .mutateWith(csrf())
                .mutateWith(mockUser())
    }
}
