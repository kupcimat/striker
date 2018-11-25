package org.saigon.striker.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.saigon.striker.model.agoda.Room;

import java.util.List;

import static org.apache.commons.lang3.Validate.notNull;

@JsonTypeName("hotel")
@JsonTypeInfo(use = Id.NAME, include = As.WRAPPER_OBJECT)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Hotel {

    private final long id;
    private final String name;
    private final List<Room> rooms;

    @JsonCreator
    public Hotel(@JsonProperty("id") long id,
                 @JsonProperty("name") String name,
                 @JsonProperty("rooms") List<Room> rooms) {
        this.id = id;
        this.name = notNull(name);
        this.rooms = List.copyOf(notNull(rooms));
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Room> getRooms() {
        return rooms;
    }
}
