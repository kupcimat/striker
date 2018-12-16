package org.saigon.striker.integration;

import org.junit.Before;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.web.reactive.server.WebTestClient;

import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * Base class for all integration tests.
 */
public class BaseIT {

    private static final String SERVER_URL = "serverUrl";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";

    @LocalServerPort
    private int port;

    @Value("${dev.username}")
    private String username;

    @Value("${dev.password}")
    private String password;

    protected WebTestClient webTestClient;

    @Before
    public void setUp() {
        checkSystemProperties();
        webTestClient = buildWebClient();
    }

    protected String serverUrl() {
        return System.getProperty(SERVER_URL, "http://localhost:" + port);
    }

    protected String username() {
        return System.getProperty(USERNAME, username);
    }

    protected String password() {
        return System.getProperty(PASSWORD, password);
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
                .defaultHeaders(headers -> headers.setBasicAuth(username(), password()))
                .build();
    }
}
