package org.saigon.striker.utils

import org.hamcrest.Description
import org.hamcrest.TypeSafeMatcher
import java.util.regex.Pattern

@Deprecated("replace with hamcrest2 matcher")
class PatternMatcher(private val pattern: Pattern) : TypeSafeMatcher<String>() {

    companion object {
        fun matchesPattern(regex: String): PatternMatcher {
            return PatternMatcher(Pattern.compile(regex))
        }
    }

    override fun matchesSafely(item: String): Boolean {
        return pattern.matcher(item).matches()
    }

    override fun describeTo(description: Description) {
        description.appendText("a string matching ").appendValue(pattern.pattern())
    }

    override fun describeMismatchSafely(item: String, mismatchDescription: Description) {
        mismatchDescription.appendText("Expecting ").appendDescriptionOf(this)
    }
}
