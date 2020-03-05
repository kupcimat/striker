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

object TestUtils

val defaultMatchers: Map<String, Matcher<String>> = mapOf(
    "request-id" to matchesPattern("[a-z0-9]+"),
    "iso-date-time" to matchesPattern("[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}\\.[0-9]{3,6}Z")
)

fun authenticate(client: WebTestClient): WebTestClient {
    return client
        .mutateWith(csrf())
        .mutateWith(mockUser())
}

fun readFile(filename: String): String {
    return TestUtils.javaClass
        .getResource("/$filename")
        .readText(Charsets.UTF_8)
}

// Using overloaded functions because groovy doesn't work with kotlin default parameter values
fun jsonEquals(expectedJsonFile: String): Matcher<String> {
    return jsonEquals(expectedJsonFile, mapOf())
}

fun jsonEquals(expectedJsonFile: String, extraMatchers: Map<String, Matcher<String>>): Matcher<String> {
    val fileContent = readFile(expectedJsonFile)

    // In case of no content return null matcher
    if (fileContent.isEmpty()) {
        return Matchers.nullValue(String::class.java)
    }
    return JsonMatchers.jsonStringEquals(fileContent)
        .apply {
            defaultMatchers.forEach { (name, matcher) -> withMatcher(name, matcher) }
            extraMatchers.forEach { (name, matcher) -> withMatcher(name, matcher) }
        }
}

fun assertJson(actual: Any, expectedJsonFile: String) {
    val actualJson = ObjectMapper().writeValueAsString(actual)

    assertThat(actualJson, jsonEquals(expectedJsonFile))
}
