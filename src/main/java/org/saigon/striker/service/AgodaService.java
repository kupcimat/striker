package org.saigon.striker.service;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.regex.Pattern;

import static java.lang.String.format;

@Service
public class AgodaService {

    public static final String AGODA_BASE_URL = "https://www.agoda.com";
    public static final String AGODA_COOKIE_NAME = "agoda.version.03";

    private static final Pattern COOKIE_ID_PATTERN = Pattern.compile(
            "CookieId=([0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12})");

    private final WebClient webClient;

    public AgodaService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(AGODA_BASE_URL).build();
    }

    public Mono<String> getAgodaCookie() {
        // TODO error handling
        return webClient.get().accept(MediaType.TEXT_HTML)
                .exchange()
                .flatMap(clientResponse -> clientResponse.toEntity(String.class))
                .map(responseEntity -> getCookieId(responseEntity.getHeaders().get(HttpHeaders.SET_COOKIE)));
    }

    public Mono<ClientResponse> callAgodaApi(String uri, String currency) {
        // TODO error handling
        return getAgodaCookie().flatMap(cookieId ->
                webClient.get()
                        .uri(uri).accept(MediaType.APPLICATION_JSON).cookie(AGODA_COOKIE_NAME, createCookie(cookieId, currency))
                        .exchange());
    }

    public static String createCookie(String cookieId, String currency) {
        return format("CookieId=%s&CurLabel=%s", cookieId, currency);
    }

    private static String getCookieId(List<String> cookies) {
        if (cookies == null) {
            throw new IllegalStateException("Cookie headers are missing");
        }
        return cookies.stream()
                .filter(cookie -> cookie.contains(AGODA_COOKIE_NAME))
                .map(AgodaService::parseCookieId)
                .findFirst().orElseThrow(() -> new IllegalStateException(format("Cookie '%s' is missing", AGODA_COOKIE_NAME)));
    }

    private static String parseCookieId(String cookie) {
        var matcher = COOKIE_ID_PATTERN.matcher(cookie);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            throw new IllegalStateException(format("Cookie '%s' is missing CookieId", AGODA_COOKIE_NAME));
        }
    }
}
