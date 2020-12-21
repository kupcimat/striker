package org.saigon.striker.controller

import org.saigon.striker.model.AgodaParameters
import org.saigon.striker.service.AgodaService
import org.saigon.striker.service.ValidationException
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.queryParamOrNull

class AgodaHandler(private val agodaService: AgodaService) {

    suspend fun getHotel(request: ServerRequest): ServerResponse {
        val parameters = AgodaParameters(
            getIntQueryParam(request, "hotelId"),
            getStringQueryParam(request, "checkInDate"),
            getIntQueryParam(request, "lengthOfStay"),
            getIntQueryParam(request, "rooms"),
            getIntQueryParam(request, "adults"),
            getIntQueryParam(request, "children"),
            getStringQueryParam(request, "currency")
        )
        return ok().bodyValueAndAwait(agodaService.getHotel(parameters))
    }

    suspend fun search(request: ServerRequest): ServerResponse {
        val query = getStringQueryParam(request, "query")
        return ok().bodyValueAndAwait(agodaService.search(query))
    }

    fun getStringQueryParam(request: ServerRequest, name: String): String {
        return request.queryParamOrNull(name)
            ?: throw ValidationException("Required string query parameter is missing: $name")
    }

    fun getIntQueryParam(request: ServerRequest, name: String): Int {
        return request.queryParamOrNull(name)?.toIntOrNull()
            ?: throw ValidationException("Required int query parameter is missing or invalid: $name")
    }
}
