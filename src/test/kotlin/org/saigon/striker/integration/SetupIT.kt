package org.saigon.striker.integration

import org.saigon.striker.config.FixturesProperties
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.reactive.server.WebTestClient
import java.lang.System.getProperty
import java.nio.charset.StandardCharsets.UTF_8
import java.time.Duration

/**
 * Base class for all integration tests.
 */
open class SetupIT(properties: FixturesProperties? = null, port: Int? = null) {

    // TODO externalize somewhere?
    private val serverUrlProperty = "serverUrl"
    private val usernameProperty = "username"
    private val passwordProperty = "password"
    private val webClientTimeout = Duration.ofSeconds(30)

    init {
        if (getProperty(serverUrlProperty).isNullOrEmpty()) {
            validateLocalRunProperties(properties, port)
        } else {
            validateRemoteRunProperties()
        }
    }

    private val serverUrl: String = getProperty(serverUrlProperty, "http://localhost:$port")
    private val username: String = getProperty(usernameProperty, properties?.username)
    private val password: String = getProperty(passwordProperty, properties?.password)

    val webTestClient: WebTestClient = WebTestClient.bindToServer()
        .baseUrl(serverUrl)
        .defaultHeaders { it.accept = listOf(APPLICATION_JSON) }
        .defaultHeaders { it.setBasicAuth(username, password, UTF_8) }
        .responseTimeout(webClientTimeout)
        .build()

    private fun validateLocalRunProperties(properties: FixturesProperties?, port: Int?) {
        if ((properties == null) || (port == null)) {
            throw IllegalStateException(
                "$serverUrlProperty system property is not defined, please define it together with "
                        + "'$usernameProperty' and '$passwordProperty' or run as a spring boot test."
            )
        }
    }

    private fun validateRemoteRunProperties() {
        if (getProperty(usernameProperty).isNullOrEmpty()) {
            throw IllegalStateException("'$usernameProperty' system property is not defined.")
        }
        if (getProperty(passwordProperty).isNullOrEmpty()) {
            throw IllegalStateException("'$passwordProperty' system property is not defined.")
        }
    }
}
