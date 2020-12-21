package org.saigon.striker

import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import java.nio.charset.StandardCharsets
import java.time.Duration

open class SetupIT2 : SetupTest(application) {

    // TODO do not use constants
    private val serverUrl = "http://localhost:8080"
    private val username = "username"
    private val password = "password"

    val webTestClient: WebTestClient = WebTestClient.bindToServer()
        .baseUrl(serverUrl)
        .defaultHeaders { it.accept = listOf(MediaType.APPLICATION_JSON) }
        .defaultHeaders { it.setBasicAuth(username, password, StandardCharsets.UTF_8) }
        .responseTimeout(Duration.ofSeconds(30))
        .build()
}
