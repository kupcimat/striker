package org.saigon.striker.integration;

import org.junit.jupiter.api.Test;

public class BasicIT extends BaseIT {

    @Test
    public void name() {
        webTestClient.get().uri("/agoda")
                .exchange()
                .expectStatus().isOk();
    }
}
