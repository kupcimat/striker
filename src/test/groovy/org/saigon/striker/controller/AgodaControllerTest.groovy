package org.saigon.striker.controller

import kotlin.coroutines.Continuation
import org.hamcrest.Matchers
import org.saigon.striker.model.AgodaParameters
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
    def "GET agoda hotel (params = #queryParams)"() {
        given:
        agodaService.getHotel(agodaParams, _ as Continuation) >> new Hotel(42, "my-hotel", [])

        expect:
        api(webTestClient).get().uri("/agoda${createQueryString(queryParams)}")
                .exchange()
                .expectStatus().isEqualTo(expectedStatus)
                .expectBody(String).value(jsonEquals(expectedJson, ["error-message": Matchers.is(errorMessage)]))

        where:
        expectedStatus         | expectedJson                        | errorMessage
        HttpStatus.BAD_REQUEST | "agoda-400-invalid-parameters.json" | "Required int parameter 'hotelId' is not present"
        HttpStatus.BAD_REQUEST | "agoda-400-invalid-parameters.json" | "Required String parameter 'checkInDate' is not present"
        HttpStatus.BAD_REQUEST | "agoda-400-invalid-parameters.json" | "Required int parameter 'lengthOfStay' is not present"
        HttpStatus.BAD_REQUEST | "agoda-400-invalid-parameters.json" | "Required int parameter 'rooms' is not present"
        HttpStatus.BAD_REQUEST | "agoda-400-invalid-parameters.json" | "Required int parameter 'adults' is not present"
        HttpStatus.BAD_REQUEST | "agoda-400-invalid-parameters.json" | "Required int parameter 'children' is not present"
        HttpStatus.BAD_REQUEST | "agoda-400-invalid-parameters.json" | "Type mismatch."
        HttpStatus.BAD_REQUEST | "agoda-400-invalid-parameters.json" | "Type mismatch."
        HttpStatus.BAD_REQUEST | "agoda-400-invalid-parameters.json" | "Type mismatch."
        HttpStatus.BAD_REQUEST | "agoda-400-invalid-parameters.json" | "Type mismatch."
        HttpStatus.BAD_REQUEST | "agoda-400-invalid-parameters.json" | "Type mismatch."
        HttpStatus.OK          | "agoda-200-ok-empty.json"           | null
        HttpStatus.OK          | "agoda-200-ok-empty.json"           | null

        queryParams << [
                [:],
                [hotelId: 42],
                [hotelId: 42, checkInDate: "2020-05-25"],
                [hotelId: 42, checkInDate: "2020-05-25", lengthOfStay: 10],
                [hotelId: 42, checkInDate: "2020-05-25", lengthOfStay: 10, rooms: 20],
                [hotelId: 42, checkInDate: "2020-05-25", lengthOfStay: 10, rooms: 20, adults: 30],
                [hotelId: "", checkInDate: "2020-05-25", lengthOfStay: 10, rooms: 20, adults: 30, children: 40],
                [hotelId: 42, checkInDate: "2020-05-25", lengthOfStay: "", rooms: 20, adults: 30, children: 40],
                [hotelId: 42, checkInDate: "2020-05-25", lengthOfStay: 10, rooms: "", adults: 30, children: 40],
                [hotelId: 42, checkInDate: "2020-05-25", lengthOfStay: 10, rooms: 20, adults: "", children: 40],
                [hotelId: 42, checkInDate: "2020-05-25", lengthOfStay: 10, rooms: 20, adults: 30, children: ""],
                [hotelId: 42, checkInDate: "2020-05-25", lengthOfStay: 10, rooms: 20, adults: 30, children: 40],
                [hotelId: 42, checkInDate: "2020-05-25", lengthOfStay: 10, rooms: 20, adults: 30, children: 40, currency: "EUR"]
        ]
        agodaParams << [
                null, null, null, null, null, null, null, null, null, null, null,
                new AgodaParameters(42, "2020-05-25", 10, 20, 30, 40, "VND"),
                new AgodaParameters(42, "2020-05-25", 10, 20, 30, 40, "EUR")
        ]
    }

    String createQueryString(Map<String, Object> queryParams) {
        if (queryParams.isEmpty()) return ""

        def formattedQueryParams = queryParams.collect { "${it.key}=${it.value}" }.join("&")
        return "?$formattedQueryParams"
    }
}
