package org.saigon.striker

import groovy.transform.CompileStatic
import org.hamcrest.Description
import org.hamcrest.TypeSafeMatcher

import java.util.regex.Pattern

import static org.apache.commons.lang3.Validate.notNull

@CompileStatic
class PatternMatcher extends TypeSafeMatcher<String> {

    private final Pattern pattern

    PatternMatcher(Pattern pattern) {
        this.pattern = notNull(pattern, "pattern cannot be null")
    }

    static PatternMatcher matchesPattern(String regex) {
        notNull(regex, "regex cannot be null")
        return new PatternMatcher(Pattern.compile(regex))
    }

    @Override
    protected boolean matchesSafely(String item) {
        return pattern.matcher(item).matches()
    }

    @Override
    void describeTo(Description description) {
        description.appendText("a string matching ").appendValue(pattern.pattern())
    }

    @Override
    protected void describeMismatchSafely(String item, Description mismatchDescription) {
        mismatchDescription.appendText("Expecting ").appendDescriptionOf(this)
    }
}
