package org.saigon.striker.model

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.ObjectCodec
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import org.springframework.boot.jackson.JsonObjectDeserializer
import java.io.IOException

@JsonDeserialize(using = AgodaHotelDeserializer::class)
data class AgodaHotel(val id: Long,
                      val name: String,
                      val rooms: List<Room>)

class AgodaHotelDeserializer : JsonObjectDeserializer<AgodaHotel>() {

    @Throws(IOException::class)
    override fun deserializeObject(jsonParser: JsonParser,
                                   context: DeserializationContext,
                                   codec: ObjectCodec,
                                   tree: JsonNode): AgodaHotel {

        val hotelId = getRequiredNode(tree, "hotelId").longValue()
        val hotelName = getRequiredNode(getRequiredNode(tree, "hotelInfo"), "name").textValue()
        val roomsData = getRequiredNode(getRequiredNode(tree, "roomGridData"), "masterRooms")

        val rooms = roomsData.map { roomNode ->
            Room(
                    getRequiredNode(roomNode, "id").longValue(),
                    getRequiredNode(roomNode, "name").textValue(),
                    getRoomVariants(getRequiredNode(roomNode, "rooms")))
        }.toList()

        return AgodaHotel(hotelId, hotelName, rooms)
    }

    private fun getRoomVariants(roomVariantsData: JsonNode): List<RoomVariant> {
        return roomVariantsData.map { roomVariantNode ->
            RoomVariant(
                    getRequiredNode(roomVariantNode, "uid").textValue(),
                    getRequiredNode(getRequiredNode(roomVariantNode, "inclusivePrice"), "display").doubleValue(),
                    getRequiredNode(getRequiredNode(roomVariantNode, "totalPrice"), "display").doubleValue(),
                    getRequiredNode(roomVariantNode, "currency").textValue(),
                    roomVariantNode.get("payLater")?.let { getRequiredNode(it, "isAvailable").booleanValue() } ?: false,
                    roomVariantNode.get("payAtHotel")?.let { getRequiredNode(it, "isAvailable").booleanValue() } ?: false,
                    getRequiredNode(roomVariantNode, "isFreeCancellation").booleanValue(),
                    getRequiredNode(roomVariantNode, "isBreakfastIncluded").booleanValue())
        }.toList()
    }
}
