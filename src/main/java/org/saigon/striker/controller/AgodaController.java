package org.saigon.striker.controller;

import org.saigon.striker.model.Hotel;
import org.saigon.striker.model.AgodaHotel;
import org.saigon.striker.service.AgodaService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import static org.saigon.striker.service.AgodaService.AGODA_COOKIE_NAME;
import static org.saigon.striker.service.AgodaService.createCookie;

@RestController
@RequestMapping(AgodaController.BASE_URI)
public class AgodaController {

    static final String BASE_URI = "/agoda";

    private final WebClient webClient;
    private final AgodaService agodaService;

    public AgodaController(WebClient.Builder webClientBuilder, AgodaService agodaService) {
        this.webClient = webClientBuilder.baseUrl(AgodaService.AGODA_BASE_URL).build();
        this.agodaService = agodaService;
    }

    @GetMapping
    public Mono<Hotel> getAgodaPOC(@RequestParam(name = "currency", defaultValue = "VND") String currency) {
        // TODO error handling
        return agodaService.getAgodaCookie()
                .flatMap(cookieId -> webClient.get()
                        .uri(createPropertyUri()).accept(MediaType.APPLICATION_JSON).cookie(AGODA_COOKIE_NAME, createCookie(cookieId, currency))
                        .retrieve()
                        .bodyToMono(AgodaHotel.class))
                .map(hotelInput -> new Hotel(hotelInput.getId(), hotelInput.getName(), hotelInput.getRooms()));
    }

    private String createPropertyUri() {
        return UriComponentsBuilder.fromPath("/api/en-us/pageparams/property")
                .queryParam("hotel_id", "1157572")
                .queryParam("checkIn", "2018-12-05")
                .queryParam("los", "7")
                .queryParam("rooms", "1")
                .queryParam("adults", "2")
                .queryParam("childs", "0")
                .build().toUri().toString();
    }
}
