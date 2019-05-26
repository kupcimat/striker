package org.saigon.striker

import org.junit.jupiter.params.provider.MethodSource
import org.saigon.striker.model.UserEntity

@MethodSource("org.saigon.striker.ParameterizedTestSourcesKt#userEntityFactory")
annotation class UserEntitySource

@Suppress("unused")
fun userEntityFactory(): List<UserEntity> {
    return listOf(
        UserEntity(username = "username", password = "password", id = "id"),
        UserEntity(username = "マット", password = "こんにちは", id = "id")
    )
}
