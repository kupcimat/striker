package org.saigon.striker.controller;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.List;

import static org.apache.commons.lang3.Validate.notNull;

@JsonTypeName("error")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT)
public class ErrorResponse {

    private final List<String> messages;

    public ErrorResponse(String message) {
        this(message != null ? List.of(message) : List.of());
    }

    @JsonCreator
    public ErrorResponse(@JsonProperty("messages") List<String> messages) {
        this.messages = List.copyOf(notNull(messages));
    }

    public List<String> getMessages() {
        return messages;
    }
}
