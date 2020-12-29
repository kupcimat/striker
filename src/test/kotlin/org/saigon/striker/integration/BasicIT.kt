package org.saigon.striker.integration

import org.junit.jupiter.api.Test
import org.saigon.striker.SetupIT2
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
            "children" to "0",
            "currency" to "VND"
        ).entries.joinToString(separator = "&")

        webTestClient.get().uri("/api/agoda?$queryParamsString")
            .exchange()
            // TODO how to do integration tests?
            .expectStatus().isOk
            .expectBody<String>()
    }
}

class BasicIT : SetupIT(), BasicScenario

class BasicITLocalTest : SetupIT2(), BasicScenario
