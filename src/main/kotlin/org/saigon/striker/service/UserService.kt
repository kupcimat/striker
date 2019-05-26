package org.saigon.striker.service

import org.saigon.striker.model.UserEntity
import org.saigon.striker.model.UserRepository
import org.saigon.striker.model.UserRoles
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class UserService(
    val userRepository: UserRepository,
    val passwordEncoder: PasswordEncoder
) : ReactiveUserDetailsService {

    override fun findByUsername(username: String?): Mono<UserDetails> {
        return userRepository.findByUsernameMono(username ?: throw IllegalArgumentException())
            .map(UserEntity::toUserDetails)
    }

    suspend fun createUser(userEntity: UserEntity): UserEntity {
        if (userRepository.findByUsername(userEntity.username) != null) {
            throw UsernameAlreadyExistsException(userEntity.username)
        }
        return userRepository.save(userEntity.encodePassword(passwordEncoder))
    }

    suspend fun getUser(id: String): UserEntity? = userRepository.findById(id)

    suspend fun getUserByUsername(username: String): UserEntity? = userRepository.findByUsername(username)

    suspend fun deleteUser(id: String): Unit = userRepository.deleteById(id)
}

private fun UserEntity.toUserDetails(): UserDetails = User.builder()
    .username(username)
    .password(password)
    .roles(UserRoles.USER.toString())
    .build()

private fun UserEntity.encodePassword(passwordEncoder: PasswordEncoder): UserEntity =
    copy(password = passwordEncoder.encode(password))
