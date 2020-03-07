package org.saigon.striker.controller

import kotlin.coroutines.Continuation
import org.saigon.striker.model.User
import org.saigon.striker.model.UserEntity
import org.saigon.striker.service.UserService
import org.saigon.striker.service.UsernameAlreadyExistsException
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.http.HttpStatus
import org.springframework.test.web.reactive.server.WebTestClient
import spock.lang.Specification
import spock.lang.Unroll

import static org.saigon.striker.utils.TestUtilsKt.authenticate
import static org.saigon.striker.utils.TestUtilsKt.jsonEquals

@WebFluxTest(UserController)
class UserControllerTest extends Specification {

    static final String USER_ID = "userId"
    static final String USERNAME = "username"
    static final String PASSWORD = "password"
    static final UserEntity USER_ENTITY = new UserEntity(USERNAME, PASSWORD, USER_ID)

    @Autowired
    WebTestClient webTestClient

    @SpringBean
    UserService userService = Mock()

    @Unroll
    def "POST user (#expectedJson)"() {
        given:
        userService.createUser(_ as UserEntity, _ as Continuation) >> { mockedUserSupplier() }

        expect:
        authenticate(webTestClient).post().uri("/api/admin/users")
                .bodyValue(inputUser)
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

        mockedUserSupplier << [
                { USER_ENTITY },
                { throw new UsernameAlreadyExistsException(USERNAME) },
                { null },
                { null },
                { null },
                { null }
        ]
    }

    @Unroll
    def "GET user (#expectedJson)"() {
        given:
        userService.getUser(USER_ID, _ as Continuation) >> mockedUser

        expect:
        authenticate(webTestClient).get().uri("/api/admin/users/$USER_ID")
                .exchange()
                .expectStatus().isEqualTo(expectedStatus)
                .expectBody(String).value(jsonEquals(expectedJson))

        where:
        mockedUser  | expectedStatus       | expectedJson
        USER_ENTITY | HttpStatus.OK        | "user-200-ok.json"
        null        | HttpStatus.NOT_FOUND | "empty-response.json"
    }

    def "DELETE user"() {
        when:
        authenticate(webTestClient).delete().uri("/api/admin/users/$USER_ID")
                .exchange()
                .expectStatus().isNoContent()
                .expectBody().isEmpty()

        then:
        1 * userService.deleteUser(USER_ID, _ as Continuation)
    }

    def "GET user unauthorized"() {
        expect:
        webTestClient.get().uri("/api/admin/users/$USER_ID")
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody().isEmpty()
    }
}
