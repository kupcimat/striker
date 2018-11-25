package org.saigon.striker.model.agoda;

import java.util.List;

import static org.apache.commons.lang3.Validate.notNull;

public class AgodaHotel {

    private final long id;
    private final String name;
    private final List<Room> rooms;

    public AgodaHotel(long id, String name, List<Room> rooms) {
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
