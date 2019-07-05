package org.saigon.striker.service

import org.saigon.striker.model.*
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.http.MediaType.TEXT_HTML
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import org.springframework.web.reactive.function.client.awaitEntity
import org.springframework.web.reactive.function.client.awaitExchange
import org.springframework.web.util.UriComponentsBuilder

@Component
class AgodaService(webClientBuilder: WebClient.Builder, baseUrl: String = "https://www.agoda.com") {

    private val cookieName = "agoda.version.03"
    private val cookieIdRegex = Regex("CookieId=([0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12})")

    val webClient = webClientBuilder.baseUrl(baseUrl).build()

    suspend fun getHotel(parameters: AgodaParameters): Hotel {
        val agodaHotel = webClient.get()
            .uri(createHotelUri(parameters))
            .accept(APPLICATION_JSON)
            .cookie(cookieName, createCookie(getCookieId(), parameters.currency))
            .retrieve()
            .onStatus(HttpStatus::isError) { Exceptions.handleAgodaError(it) }
            .awaitBody<AgodaHotel>()

        return agodaHotel.toHotel()
    }

    suspend fun search(query: String): SearchResult {
        val agodaSearchResult = webClient.get()
            .uri(createSearchUri(query))
            .accept(APPLICATION_JSON)
            .retrieve()
            .onStatus(HttpStatus::isError) { Exceptions.handleAgodaError(it) }
            .awaitBody<AgodaSearchResult>()

        return agodaSearchResult.toSearchResult()
    }

    suspend fun getCookieId(): String {
        val responseEntity = webClient.get()
            .uri("/")
            .accept(TEXT_HTML)
            .awaitExchange()
            .awaitEntity<String>() // retrieve body to release resources

        if (responseEntity.statusCode.isError) {
            throw AgodaApiException(responseEntity.statusCode)
        }
        return getCookieId(responseEntity.headers[HttpHeaders.SET_COOKIE])
    }

    private fun getCookieId(cookies: List<String>?): String {
        val cookieValue = cookies?.find { it.contains(cookieName) }
            ?: throw IllegalStateException("Agoda cookie is missing")

        return cookieIdRegex.find(cookieValue)?.groupValues?.get(1)
            ?: throw IllegalStateException("Agoda cookie id is missing")
    }

    private fun createCookie(cookieId: String, currency: String): String {
        return "CookieId=$cookieId&CurLabel=$currency"
    }

    private fun createHotelUri(parameters: AgodaParameters): String {
        return UriComponentsBuilder.fromPath("/api/en-us/pageparams/property")
            .queryParam("hotel_id", parameters.hotelId)
            .queryParam("checkIn", parameters.checkInDate)
            .queryParam("los", parameters.lengthOfStay)
            .queryParam("rooms", parameters.rooms)
            .queryParam("adults", parameters.adults)
            .queryParam("childs", parameters.children)
            .toUriString()
    }

    private fun createSearchUri(query: String): String {
        return UriComponentsBuilder.fromPath("/Search/Search/GetUnifiedSuggestResult/3/1/1/0/en-us")
            .queryParam("searchText", query)
            .queryParam("isHotelLandSearch", "true")
            .toUriString()
    }
}
