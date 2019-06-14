package org.saigon.striker.controller

import kotlin.coroutines.Continuation
import org.saigon.striker.model.Hotel
import org.saigon.striker.service.AgodaService
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.http.HttpStatus
import org.springframework.test.web.reactive.server.WebTestClient
import spock.lang.Specification
import spock.lang.Unroll

import static org.saigon.striker.utils.TestUtilsKt.api
import static org.saigon.striker.utils.TestUtilsKt.jsonEquals

@WebFluxTest(AgodaController)
class AgodaControllerTest extends Specification {

    @Autowired
    WebTestClient webTestClient

    @SpringBean
    AgodaService agodaService = Stub()

    @Unroll
    def "GET agoda hotel (params = #inputParams)"() {
        given:
        agodaService.getHotel(inputCurrency, _ as Continuation) >> mockedHotel

        expect:
        api(webTestClient).get().uri("/agoda$inputParams")
                .exchange()
                .expectStatus().isEqualTo(expectedStatus)
                .expectBody(String).value(jsonEquals(expectedJson))

        where:
        inputParams     | inputCurrency | mockedHotel                  | expectedStatus | expectedJson
        ""              | "VND"         | new Hotel(1, "my-hotel", []) | HttpStatus.OK  | "agoda-200-ok-empty.json"
        "?currency=EUR" | "EUR"         | new Hotel(1, "my-hotel", []) | HttpStatus.OK  | "agoda-200-ok-empty.json"
    }
}
