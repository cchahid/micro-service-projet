package com.buberdinner.reservationservice.domain.valueObject;

import java.util.Objects;

public record GuestId(Long value) {
    public GuestId {
        Objects.requireNonNull(value);
    }



    @Override
    public Long value() {
        return value;
    }

    public static GuestId generate(long value){
        return new GuestId(value);
    }


}