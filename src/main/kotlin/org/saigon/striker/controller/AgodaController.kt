package org.saigon.striker.controller

import org.saigon.striker.model.AgodaParameters
import org.saigon.striker.model.Hotel
import org.saigon.striker.service.AgodaService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class AgodaController(val agodaService: AgodaService) {

    @GetMapping(UriTemplates.AGODA)
    suspend fun getHotel(
        @RequestParam hotelId: Int,
        @RequestParam checkInDate: String,
        @RequestParam lengthOfStay: Int,
        @RequestParam rooms: Int,
        @RequestParam adults: Int,
        @RequestParam children: Int,
        @RequestParam(defaultValue = "VND") currency: String
    ): ResponseEntity<Hotel> {
        val parameters = AgodaParameters(hotelId, checkInDate, lengthOfStay, rooms, adults, children, currency)

        return ResponseEntity.ok(agodaService.getHotel(parameters))
    }
}
