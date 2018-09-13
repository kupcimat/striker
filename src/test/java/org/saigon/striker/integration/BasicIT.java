package org.saigon.striker.integration;

import org.junit.Test;

public class BasicIT extends BaseIT {

    @Test
    public void name() {
        webTestClient.get().uri("/resolution")
                .exchange()
                .expectStatus().isOk();
    }
}
