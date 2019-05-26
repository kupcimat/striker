package org.saigon.striker.controller

import org.springframework.web.util.UriTemplate
import java.net.URI

object UriTemplates {
    const val USERS: String = "/admin/users"
    const val USER: String = "$USERS/{userId}"

    fun expandUser(userId: String?): URI = UriTemplate(USER).expand(userId ?: throw IllegalArgumentException())
}
