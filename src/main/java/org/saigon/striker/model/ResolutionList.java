package org.saigon.striker.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.List;

@JsonTypeName("resolutions")
@JsonTypeInfo(use = Id.NAME, include = As.WRAPPER_OBJECT)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResolutionList {

    private final List<Resolution> items;

    @JsonCreator
    public ResolutionList(@JsonProperty("items") List<Resolution> items) {
        // TODO validation
        this.items = items;
    }

    public List<Resolution> getItems() {
        return items;
    }
}
