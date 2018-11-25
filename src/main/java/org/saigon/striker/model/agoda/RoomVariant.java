package org.saigon.striker.model.agoda;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.annotation.JsonTypeName;

import static org.apache.commons.lang3.Validate.notNull;

@JsonTypeName("variant")
@JsonTypeInfo(use = Id.NAME, include = As.WRAPPER_OBJECT)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RoomVariant {

    private final String id;
    private final double pricePerNight;
    private final double priceTotal;
    private final String currency;
    private final boolean payLater;
    private final boolean payAtHotel;
    private final boolean freeCancellation;
    private final boolean breakfastIncluded;

    @JsonCreator
    public RoomVariant(@JsonProperty("id") String id,
                       @JsonProperty("pricePerNight") double pricePerNight,
                       @JsonProperty("priceTotal") double priceTotal,
                       @JsonProperty("currency") String currency,
                       @JsonProperty("payLater") boolean payLater,
                       @JsonProperty("payAtHotel") boolean payAtHotel,
                       @JsonProperty("freeCancellation") boolean freeCancellation,
                       @JsonProperty("breakfastIncluded") boolean breakfastIncluded) {
        this.id = notNull(id);
        this.pricePerNight = pricePerNight;
        this.priceTotal = priceTotal;
        this.currency = notNull(currency);
        this.payLater = payLater;
        this.payAtHotel = payAtHotel;
        this.freeCancellation = freeCancellation;
        this.breakfastIncluded = breakfastIncluded;
    }

    public String getId() {
        return id;
    }

    public double getPricePerNight() {
        return pricePerNight;
    }

    public double getPriceTotal() {
        return priceTotal;
    }

    public String getCurrency() {
        return currency;
    }

    public boolean isPayLater() {
        return payLater;
    }

    public boolean isPayAtHotel() {
        return payAtHotel;
    }

    public boolean isFreeCancellation() {
        return freeCancellation;
    }

    public boolean isBreakfastIncluded() {
        return breakfastIncluded;
    }
}
