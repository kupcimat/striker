package org.saigon.striker.utils

import com.fasterxml.jackson.databind.ObjectMapper
import net.javacrumbs.jsonunit.JsonMatchers
import org.hamcrest.Matcher
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.matchesPattern
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockUser
import org.springframework.test.web.reactive.server.WebTestClient

object RegexPatterns {
    const val REQUEST_ID = "[a-z0-9]+"
    const val ISO_DATE_TIME = "[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}\\.[0-9]{6}Z"
}

fun api(client: WebTestClient): WebTestClient {
    return client
        .mutateWith(csrf())
        .mutateWith(mockUser())
}

fun readFile(filename: String): String {
    return RegexPatterns.javaClass
        .getResource("/$filename")
        .readText(Charsets.UTF_8)
}

fun jsonEquals(expectedJsonFile: String): Matcher<String> {
    val fileContent = readFile(expectedJsonFile)

    // In case of no content return null matcher
    if (fileContent.isEmpty()) {
        return Matchers.nullValue(String::class.java)
    }
    return JsonMatchers.jsonStringEquals(fileContent)
        .withMatcher("request-id", matchesPattern(RegexPatterns.REQUEST_ID))
        .withMatcher("iso-date-time", matchesPattern(RegexPatterns.ISO_DATE_TIME))
}

fun assertJson(actual: Any, expectedJsonFile: String) {
    val actualJson = ObjectMapper().writeValueAsString(actual)

    assertThat(actualJson, jsonEquals(expectedJsonFile))
}
