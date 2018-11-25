package org.saigon.striker.model.agoda;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.List;

import static org.apache.commons.lang3.Validate.notNull;

@JsonTypeName("room")
@JsonTypeInfo(use = Id.NAME, include = As.WRAPPER_OBJECT)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Room {

    private final long id;
    private final String name;
    private final List<RoomVariant> variants;

    @JsonCreator
    public Room(@JsonProperty("id") long id,
                @JsonProperty("name") String name,
                @JsonProperty("variants") List<RoomVariant> variants) {
        this.id = id;
        this.name = notNull(name);
        this.variants = List.copyOf(notNull(variants));
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<RoomVariant> getVariants() {
        return variants;
    }
}
