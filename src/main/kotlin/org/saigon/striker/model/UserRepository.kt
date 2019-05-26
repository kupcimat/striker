package org.saigon.striker.model

import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.data.mongodb.core.ReactiveMongoOperations
import org.springframework.data.mongodb.core.find
import org.springframework.data.mongodb.core.findById
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.data.mongodb.core.remove
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class UserRepository(val mongoOperations: ReactiveMongoOperations) {

    fun findByUsernameMono(username: String): Mono<UserEntity> = mongoOperations.find<UserEntity>(
        Query(UserEntity::username isEqualTo username)
    ).singleOrEmpty()

    suspend fun findByUsername(username: String): UserEntity? = findByUsernameMono(username).awaitFirstOrNull()

    suspend fun findById(id: String): UserEntity? = mongoOperations.findById<UserEntity>(id).awaitFirstOrNull()

    suspend fun save(userEntity: UserEntity): UserEntity = mongoOperations.save(userEntity).awaitFirst()

    suspend fun deleteById(id: String) {
        mongoOperations.remove<UserEntity>(
            Query(UserEntity::id isEqualTo id)
        ).awaitFirst()
    }
}
