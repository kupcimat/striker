package org.saigon.striker.controller

import org.springframework.web.util.UriTemplate
import java.net.URI

object UriTemplates {

    private const val API_PREFIX = "/api"

    const val AGODA = "$API_PREFIX/agoda"
    const val AGODA_SEARCH = "$AGODA/search"

    const val USERS = "$API_PREFIX/admin/users"
    const val USER = "$USERS/{userId}"

    fun expandUser(userId: String?): URI = UriTemplate(USER).expand(userId ?: throw IllegalArgumentException())
}
