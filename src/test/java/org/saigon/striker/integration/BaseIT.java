package org.saigon.striker.integration;

import org.junit.jupiter.api.BeforeEach;
import org.saigon.striker.config.FixturesProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.springframework.http.MediaType.APPLICATION_JSON;

/**
 * Base class for all integration tests.
 */
public class BaseIT {

    private static final String SERVER_URL = "serverUrl";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";

    @LocalServerPort
    private int port;

    @Autowired
    private FixturesProperties properties;

    protected WebTestClient webTestClient;

    @BeforeEach
    public void setUp() {
        checkSystemProperties();
        webTestClient = buildWebClient();
    }

    protected String serverUrl() {
        return System.getProperty(SERVER_URL, "http://localhost:" + port);
    }

    protected String username() {
        return System.getProperty(USERNAME, properties.getUsername());
    }

    protected String password() {
        return System.getProperty(PASSWORD, properties.getPassword());
    }

    private void checkSystemProperties() {
        if (isNotEmpty(System.getProperty(SERVER_URL))) {
            if (isEmpty(System.getProperty(USERNAME)) || isEmpty(System.getProperty(PASSWORD)))
                throw new IllegalStateException(format(
                        "When '%s' system property is defined, then '%s' and '%s' must be defined as well",
                        SERVER_URL, USERNAME, PASSWORD));
        }
    }

    private WebTestClient buildWebClient() {
        return WebTestClient.bindToServer()
                .baseUrl(serverUrl())
                .defaultHeaders(headers -> headers.setAccept(List.of(APPLICATION_JSON)))
                .defaultHeaders(headers -> headers.setBasicAuth(username(), password()))
                .build();
    }
}
