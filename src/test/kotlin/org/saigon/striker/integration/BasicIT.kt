package org.saigon.striker.integration

import org.junit.jupiter.api.Test
import org.saigon.striker.config.FixturesProperties
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import java.time.LocalDate

interface BasicScenario {

    val webTestClient: WebTestClient

    @Test
    fun `call agoda api`() {
        val checkInDate = LocalDate.now().plusDays(30).toString()
        val queryParamsString = mapOf(
            "hotelId" to "1",
            "checkInDate" to checkInDate,
            "lengthOfStay" to "4",
            "rooms" to "1",
            "adults" to "2",
            "children" to "0"
        ).entries.joinToString(separator = "&")

        webTestClient.get().uri("/api/agoda?$queryParamsString")
            .exchange()
            .expectStatus().isOk
            .expectBody<String>()
    }
}

class BasicIT : SetupIT(), BasicScenario

@SpringBootTest(webEnvironment = RANDOM_PORT)
class BasicITLocalTest(@Autowired properties: FixturesProperties, @LocalServerPort port: Int) :
    SetupIT(properties, port), BasicScenario
