package org.saigon.striker.model

import org.saigon.striker.service.ValidationException

fun validateSize(value: String?, name: String, range: IntRange): String {
    if (value == null) {
        throw ValidationException("$name must not be null")
    }
    if (value.length !in range) {
        throw ValidationException("$name size must be between ${range.first} and ${range.last}")
    }
    return value
}
