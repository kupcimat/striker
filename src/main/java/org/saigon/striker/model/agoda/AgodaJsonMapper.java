package org.saigon.striker.model.agoda;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.boot.jackson.JsonObjectDeserializer;
import org.springframework.boot.jackson.JsonObjectSerializer;

import java.io.IOException;
import java.util.List;
import java.util.stream.StreamSupport;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

@JsonComponent
public class AgodaJsonMapper {

    public static class JsonSerializer extends JsonObjectSerializer<AgodaHotel> {
        @Override
        protected void serializeObject(AgodaHotel value,
                                       JsonGenerator jgen,
                                       SerializerProvider provider) throws IOException {

            throw new IOException("AgodaHotel serializer is not implemented");
        }
    }

    public static class JsonDeserializer extends JsonObjectDeserializer<AgodaHotel> {
        @Override
        protected AgodaHotel deserializeObject(JsonParser jsonParser,
                                               DeserializationContext context,
                                               ObjectCodec codec,
                                               JsonNode tree) throws IOException {

            var hotelId = getRequiredNode(tree, "hotelId").longValue();
            var hotelName = getRequiredNode(getRequiredNode(tree, "hotelInfo"), "name").textValue();
            var roomsIterator = getRequiredNode(getRequiredNode(tree, "roomGridData"), "masterRooms").spliterator();

            var rooms = StreamSupport.stream(roomsIterator, false)
                    .map(roomNode -> new Room(
                            getRequiredNode(roomNode, "id").longValue(),
                            getRequiredNode(roomNode, "name").textValue(),
                            getRoomVariants(getRequiredNode(roomNode, "rooms"))))
                    .collect(toList());

            return new AgodaHotel(hotelId, hotelName, rooms);
        }

        private List<RoomVariant> getRoomVariants(JsonNode tree) {
            return StreamSupport.stream(tree.spliterator(), false)
                    .map(roomVariantNode -> new RoomVariant(
                            getRequiredNode(roomVariantNode, "uid").textValue(),
                            getRequiredNode(getRequiredNode(roomVariantNode, "inclusivePrice"), "display").doubleValue(),
                            getRequiredNode(getRequiredNode(roomVariantNode, "totalPrice"), "display").doubleValue(),
                            getRequiredNode(roomVariantNode, "currency").textValue(),
                            ofNullable(roomVariantNode.get("payLater"))
                                    .map(node -> getRequiredNode(node, "isAvailable").booleanValue()).orElse(false),
                            ofNullable(roomVariantNode.get("payAtHotel"))
                                    .map(node -> getRequiredNode(node, "isAvailable").booleanValue()).orElse(false),
                            getRequiredNode(roomVariantNode, "isFreeCancellation").booleanValue(),
                            getRequiredNode(roomVariantNode, "isBreakfastIncluded").booleanValue()))
                    .collect(toList());
        }
    }
}
