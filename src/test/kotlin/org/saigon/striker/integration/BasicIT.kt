package org.saigon.striker.integration

import org.junit.jupiter.api.Test
import org.saigon.striker.config.FixturesProperties
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody

interface BasicScenario {

    val webTestClient: WebTestClient

    @Test
    fun `call agoda api`() {
        // TODO dependency on agoda api?
        webTestClient.get().uri("/agoda")
            .exchange()
            .expectStatus().isOk
            .expectBody<Void>()
    }
}

class BasicIT : SetupIT(), BasicScenario

@SpringBootTest(webEnvironment = RANDOM_PORT)
class BasicITLocalTest(@Autowired properties: FixturesProperties, @LocalServerPort port: Int) :
    SetupIT(properties, port), BasicScenario
