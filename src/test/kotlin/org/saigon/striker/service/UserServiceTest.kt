package org.saigon.striker.service

import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.NullSource
import org.saigon.striker.UserEntitySource
import org.saigon.striker.model.UserEntity
import org.saigon.striker.model.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import reactor.core.publisher.Mono

@ExtendWith(MockKExtension::class)
class UserServiceTest {

    @MockK
    lateinit var userRepository: UserRepository

    @MockK
    lateinit var passwordEncoder: PasswordEncoder

    @InjectMockKs
    lateinit var userService: UserService

    @NullSource
    @UserEntitySource
    @ParameterizedTest
    fun `find user details by username`(user: UserEntity?) {
        every { userRepository.findByUsernameMono(user?.username ?: "unknown") } returns Mono.justOrEmpty(user)

        val userDetails = userService.findByUsername(user?.username ?: "unknown").block()

        if (user == null) {
            assertThat(userDetails).isNull()
        } else {
            assertThat(userDetails?.username).isEqualTo(user.username)
            assertThat(userDetails?.password).isEqualTo(user.password)
            assertThat(userDetails?.authorities).extracting("authority").containsOnly("ROLE_USER")
        }
    }

    @UserEntitySource
    @ParameterizedTest
    fun `create user`(user: UserEntity) = runBlocking<Unit> {
        coEvery { userRepository.findByUsername(user.username) } returns null
        coEvery { userRepository.save(user.copy(password = "encodedPassword")) } returns user
        every { passwordEncoder.encode(user.password) } returns "encodedPassword"

        val result = userService.createUser(user)

        assertThat(result).isEqualTo(user)
    }

    @UserEntitySource
    @ParameterizedTest
    fun `create existing user`(user: UserEntity) {
        coEvery { userRepository.findByUsername(user.username) } returns user

        val exception = assertThrows<UsernameAlreadyExistsException> {
            runBlocking { userService.createUser(user) }
        }

        assertThat(exception).hasMessageContaining("Username '${user.username}' already exists")
    }

    @NullSource
    @UserEntitySource
    @ParameterizedTest
    fun `get user by id`(user: UserEntity?) = runBlocking<Unit> {
        coEvery { userRepository.findById(user?.id ?: "unknown") } returns user

        val result = userService.getUser(user?.id ?: "unknown")

        assertThat(result).isEqualTo(user)
    }

    @NullSource
    @UserEntitySource
    @ParameterizedTest
    fun `get user by username`(user: UserEntity?) = runBlocking<Unit> {
        coEvery { userRepository.findByUsername(user?.username ?: "unknown") } returns user

        val result = userService.getUserByUsername(user?.username ?: "unknown")

        assertThat(result).isEqualTo(user)
    }

    @Test
    fun `delete user by id`() = runBlocking<Unit> {
        coEvery { userRepository.deleteById("id") } just Runs

        userService.deleteUser("id")
    }
}
