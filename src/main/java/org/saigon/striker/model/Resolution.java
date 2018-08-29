package org.saigon.striker.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("resolution")
@JsonTypeInfo(use = Id.NAME, include = As.WRAPPER_OBJECT)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Resolution {

    private final String name;
    private final Long days;

    @JsonCreator
    public Resolution(@JsonProperty("name") String name,
                      @JsonProperty("days") Long days) {
        // TODO validation
        this.name = name;
        this.days = days;
    }

    public String getName() {
        return name;
    }

    public Long getDays() {
        return days;
    }
}
