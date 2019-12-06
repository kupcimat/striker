package org.saigon.striker.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties("fixtures")
data class FixturesProperties(
    val username: String,
    val password: String
)
