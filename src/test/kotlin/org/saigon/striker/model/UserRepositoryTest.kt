package org.saigon.striker.model

import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.NullSource
import org.saigon.striker.SetupTest
import org.saigon.striker.UserEntitySource
import org.saigon.striker.dataConfig
import org.springframework.beans.factory.getBean
import org.springframework.data.mongodb.core.ReactiveMongoOperations
import org.springframework.data.mongodb.core.dropCollection
import org.springframework.fu.kofu.application

class UserRepositoryTest : SetupTest(
    application {
        enable(dataConfig)
    }
) {
    private lateinit var userRepository: UserRepository

    @BeforeEach
    fun setUp() {
        userRepository = context.getBean()
        context.getBean<ReactiveMongoOperations>().dropCollection<UserEntity>().block()
    }

    @NullSource
    @UserEntitySource
    @ParameterizedTest
    fun `find user by username using mono`(user: UserEntity?) = runBlocking<Unit> {
        user?.let { userRepository.save(it) }

        val result = userRepository.findByUsernameMono(user?.username ?: "unknown").awaitFirstOrNull()

        if (user == null) {
            assertThat(result).isNull()
        } else {
            assertThat(result?.id).isNotEmpty()
            assertThat(result?.username).isEqualTo(user.username)
            assertThat(result?.password).isEqualTo(user.password)
        }
    }

    @NullSource
    @UserEntitySource
    @ParameterizedTest
    fun `find user by username`(user: UserEntity?) = runBlocking<Unit> {
        user?.let { userRepository.save(it) }

        val result = userRepository.findByUsername(user?.username ?: "unknown")

        if (user == null) {
            assertThat(result).isNull()
        } else {
            assertThat(result?.id).isNotEmpty()
            assertThat(result?.username).isEqualTo(user.username)
            assertThat(result?.password).isEqualTo(user.password)
        }
    }

    @NullSource
    @UserEntitySource
    @ParameterizedTest
    fun `find user by id`(user: UserEntity?) = runBlocking<Unit> {
        val saved = user?.let { userRepository.save(it) }

        val result = userRepository.findById(saved?.id ?: "unknown")

        if (user == null) {
            assertThat(result).isNull()
        } else {
            assertThat(result?.id).isEqualTo(saved?.id)
            assertThat(result?.username).isEqualTo(user.username)
            assertThat(result?.password).isEqualTo(user.password)
        }
    }

    @NullSource
    @UserEntitySource
    @ParameterizedTest
    fun `delete user by id`(user: UserEntity?) = runBlocking<Unit> {
        val saved = user?.let { userRepository.save(it) }

        userRepository.deleteById(saved?.id ?: "unknown")
        val result = userRepository.findById(saved?.id ?: "unknown")

        assertThat(result).isNull()
    }
}
