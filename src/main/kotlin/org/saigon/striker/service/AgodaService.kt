package org.saigon.striker.service

import org.saigon.striker.model.AgodaHotel
import org.saigon.striker.model.Hotel
import org.saigon.striker.model.toHotel
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
import reactor.core.publisher.Mono

@Component
class AgodaService(webClientBuilder: WebClient.Builder, baseUrl: String = "https://www.agoda.com") {

    private val cookieName = "agoda.version.03"
    private val cookieIdRegex = Regex("CookieId=([0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12})")

    val webClient = webClientBuilder.baseUrl(baseUrl).build()

    suspend fun getHotel(currency: String): Hotel {
        val agodaHotel = webClient.get().uri(createUri())
            .accept(APPLICATION_JSON)
            .cookie(cookieName, createCookie(getCookieId(), currency))
            .retrieve()
            .onStatus(HttpStatus::isError) { Mono.just(AgodaApiException(it.statusCode())) }
            .awaitBody<AgodaHotel>()

        return agodaHotel.toHotel()
    }

    suspend fun getCookieId(): String {
        val response = webClient.get().uri("/")
            .accept(TEXT_HTML)
            .awaitExchange()
        // retrieve body to release resources
        val responseEntity = response.awaitEntity<String>()

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

    // TODO parametrize
    private fun createUri(): String = UriComponentsBuilder.fromPath("/api/en-us/pageparams/property")
        .queryParam("hotel_id", "1157572")
        .queryParam("checkIn", "2018-12-05")
        .queryParam("los", "7")
        .queryParam("rooms", "1")
        .queryParam("adults", "2")
        .queryParam("childs", "0")
        .toUriString()
}
