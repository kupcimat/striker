package org.saigon.striker

import groovy.transform.CompileStatic
import net.javacrumbs.jsonunit.JsonMatchers
import org.hamcrest.CoreMatchers
import org.hamcrest.Matcher

import java.nio.charset.StandardCharsets

import static org.saigon.striker.PatternMatcher.matchesPattern

@CompileStatic
class TestUtils {

    static final String REQUEST_ID_REGEX = "[a-z0-9]+"
    static final String ISO_DATE_TIME_REGEX = "[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}\\.[0-9]{6}Z"

    static String readFile(String fileName) {
        return TestUtils
                .getResource("/" + fileName)
                .getText(StandardCharsets.UTF_8.name())
    }

    static Matcher<String> jsonEquals(String fileName) {
        def fileContent = readFile(fileName)

        // in case of no content return null matcher
        if (fileContent.isEmpty()) {
            return CoreMatchers.nullValue(String)
        }

        return JsonMatchers.jsonStringEquals(fileContent)
                .withMatcher("request-id", matchesPattern(REQUEST_ID_REGEX))
                .withMatcher("iso-date-time", matchesPattern(ISO_DATE_TIME_REGEX))
    }
}
