package org.saigon.striker.controller

import org.saigon.striker.model.Hotel
import org.saigon.striker.service.AgodaService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class AgodaController(val agodaService: AgodaService) {

    @GetMapping(UriTemplates.AGODA)
    suspend fun getHotel(@RequestParam(defaultValue = "VND") currency: String): ResponseEntity<Hotel> {
        return ResponseEntity.ok(agodaService.getHotel(currency))
    }
}
