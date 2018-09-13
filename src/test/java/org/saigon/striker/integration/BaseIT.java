package org.saigon.striker.integration;

import org.junit.Before;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Base class for all integration tests.
 */
public class BaseIT {

    protected WebTestClient webTestClient;

    @Before
    public void setUp() {
        webTestClient = WebTestClient.bindToServer()
                .baseUrl(serverUrl())
                .build();
    }

    public static String localhostServerUrl(int port) {
        return "http://localhost:" + port;
    }

    public static String systemPropertyServerUrl() {
        var serverUrl = System.getProperty("serverUrl");
        if (serverUrl == null) {
            throw new IllegalStateException("System property 'serverUrl' is not set");
        }
        return serverUrl;
    }

    protected String serverUrl() {
        return systemPropertyServerUrl();
    }
}
