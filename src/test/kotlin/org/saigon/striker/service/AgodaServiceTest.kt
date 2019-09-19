package org.saigon.striker.service

import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.saigon.striker.model.AgodaParameters
import org.saigon.striker.utils.assertJson
import org.saigon.striker.utils.configure
import org.saigon.striker.utils.startMockServer
import org.saigon.striker.utils.stopMockServer
import org.springframework.web.reactive.function.client.WebClient

@ExtendWith(MockKExtension::class)
class AgodaServiceTest {

    val mockServer = startMockServer(port = 38080)
    val agodaService = AgodaService(WebClient.builder(), "http://localhost:38080")

    @AfterAll
    fun tearDown() {
        stopMockServer(mockServer)
    }

    @CsvSource(
        "200, empty-response.html",
        "204, empty-response.html"
    )
    @ParameterizedTest
    fun `get cookie id`(responseStatus: Int, responseContent: String) = runBlocking<Unit> {
        mockServer.configure {
            get("/") {
                status = responseStatus
                content = responseContent
                cookie(name = "agoda.version.03", value = "CookieId=12345678-1234-1234-1234-1234567890ab")
            }
        }

        val cookieId = agodaService.getCookieId()

        assertThat(cookieId).isEqualTo("12345678-1234-1234-1234-1234567890ab")
    }

    @CsvSource(
        "400, empty-response.html, Agoda API error: 400 BAD_REQUEST",
        "404, empty-response.html, Agoda API error: 404 NOT_FOUND",
        "500, empty-response.html, Agoda API error: 500 INTERNAL_SERVER_ERROR",
        "504, empty-response.html, Agoda API error: 504 GATEWAY_TIMEOUT"
    )
    @ParameterizedTest
    fun `get cookie id with error`(responseStatus: Int, responseContent: String, exceptionMessage: String) {
        mockServer.configure {
            get("/") {
                status = responseStatus
                content = responseContent
            }
        }

        val exception = assertThrows<AgodaApiException> {
            runBlocking { agodaService.getCookieId() }
        }

        assertThat(exception).hasMessageContaining(exceptionMessage)
    }

    @CsvSource(
        "agoda-api-mock-200-ok-single-room.json, agoda-200-ok-single-room.json",
        "agoda-api-mock-200-ok-multiple-rooms.json, agoda-200-ok-multiple-rooms.json"
    )
    @ParameterizedTest
    fun `get hotel`(apiMockJson: String, expectedJson: String) = runBlocking<Unit> {
        mockServer.configure {
            get("/") {
                status = 200
                content = "empty-response.html"
                cookie(name = "agoda.version.03", value = "CookieId=12345678-1234-1234-1234-1234567890ab")
            }
            get("/api/en-us/pageparams/property") {
                queryParam("hotel_id", "42")
                queryParam("checkIn", "2020-05-25")
                queryParam("los", "1")
                queryParam("rooms", "2")
                queryParam("adults", "3")
                queryParam("childs", "4")

                status = 200
                content = apiMockJson
            }
        }

        val hotel = agodaService.getHotel(AgodaParameters(42, "2020-05-25", 1, 2, 3, 4, "VND"))

        assertJson(hotel, expectedJson)
    }

    @CsvSource(
        "400, empty-response.html, Agoda API error: 400 BAD_REQUEST",
        "404, empty-response.html, Agoda API error: 404 NOT_FOUND",
        "500, empty-response.html, Agoda API error: 500 INTERNAL_SERVER_ERROR",
        "504, empty-response.html, Agoda API error: 504 GATEWAY_TIMEOUT"
    )
    @ParameterizedTest
    fun `get hotel with error`(responseStatus: Int, responseContent: String, exceptionMessage: String) {
        mockServer.configure {
            get("/") {
                status = 200
                content = "empty-response.html"
                cookie(name = "agoda.version.03", value = "CookieId=12345678-1234-1234-1234-1234567890ab")
            }
            get("/api/en-us/pageparams/property") {
                status = responseStatus
                content = responseContent
            }
        }

        val exception = assertThrows<AgodaApiException> {
            runBlocking { agodaService.getHotel(AgodaParameters(0, "", 0, 0, 0, 0, "")) }
        }

        assertThat(exception).hasMessageContaining(exceptionMessage)
    }

    @CsvSource(
        "agoda-search-api-mock-200-ok-single-result.json, agoda-search-200-ok-single-result.json",
        "agoda-search-api-mock-200-ok-multiple-results.json, agoda-search-200-ok-multiple-results.json"
    )
    @ParameterizedTest
    fun `agoda search`(apiMockJson: String, expectedJson: String) = runBlocking<Unit> {
        mockServer.configure {
            get("/Search/Search/GetUnifiedSuggestResult/3/1/1/0/en-us") {
                queryParam("searchText", "query")
                queryParam("isHotelLandSearch", "true")

                status = 200
                content = apiMockJson
            }
        }

        val searchResult = agodaService.search("query")

        assertJson(searchResult, expectedJson)
    }

    @CsvSource(
        "400, empty-response.json, Agoda API error: 400 BAD_REQUEST",
        "404, empty-response.json, Agoda API error: 404 NOT_FOUND",
        "500, empty-response.json, Agoda API error: 500 INTERNAL_SERVER_ERROR",
        "504, empty-response.json, Agoda API error: 504 GATEWAY_TIMEOUT"
    )
    @ParameterizedTest
    fun `agoda search with error`(responseStatus: Int, responseContent: String, exceptionMessage: String) {
        mockServer.configure {
            get("/Search/Search/GetUnifiedSuggestResult/3/1/1/0/en-us") {
                status = responseStatus
                content = responseContent
            }
        }

        val exception = assertThrows<AgodaApiException> {
            runBlocking { agodaService.search("query") }
        }

        assertThat(exception).hasMessageContaining(exceptionMessage)
    }
}
