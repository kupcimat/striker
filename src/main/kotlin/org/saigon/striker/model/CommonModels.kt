package org.saigon.striker.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeInfo.As.WRAPPER_OBJECT
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME
import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeName("hotel")
@JsonTypeInfo(use = NAME, include = WRAPPER_OBJECT)
@JsonIgnoreProperties(ignoreUnknown = true)
data class Hotel(
    val id: Long,
    val name: String,
    val rooms: List<Room>
)

@JsonTypeName("room")
@JsonTypeInfo(use = NAME, include = WRAPPER_OBJECT)
@JsonIgnoreProperties(ignoreUnknown = true)
data class Room(
    val id: Long,
    val name: String,
    val variants: List<RoomVariant>
)

@JsonTypeName("variant")
@JsonTypeInfo(use = NAME, include = WRAPPER_OBJECT)
@JsonIgnoreProperties(ignoreUnknown = true)
data class RoomVariant(
    val id: String,
    val pricePerNight: Double,
    val priceTotal: Double,
    val currency: String,
    val payLater: Boolean,
    val payAtHotel: Boolean,
    val freeCancellation: Boolean,
    val breakfastIncluded: Boolean
)
