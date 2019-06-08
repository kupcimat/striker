package org.saigon.striker.controller

import org.springframework.web.util.UriTemplate
import java.net.URI

object UriTemplates {

    const val AGODA = "/agoda"

    const val USERS = "/admin/users"
    const val USER = "$USERS/{userId}"

    fun expandUser(userId: String?): URI = UriTemplate(USER).expand(userId ?: throw IllegalArgumentException())
}
