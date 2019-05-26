package org.saigon.striker.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeInfo.As.WRAPPER_OBJECT
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME
import com.fasterxml.jackson.annotation.JsonTypeName
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@JsonTypeName("user")
@JsonTypeInfo(use = NAME, include = WRAPPER_OBJECT)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(NON_NULL)
data class User(

    @field: NotNull
    @field: Size(min = 4, max = 32)
    val username: String?,

    @field: NotNull
    @field: Size(min = 8, max = 32)
    val password: String?
)

@Document(collection = "user")
@TypeAlias("userEntity")
data class UserEntity(@Indexed val username: String, val password: String, @Id val id: String? = null)

enum class UserRoles {
    USER
}

fun User.toEntity() = UserEntity(username!!, password!!)

fun UserEntity.toUserWithoutPassword() = User(username, password = null)
