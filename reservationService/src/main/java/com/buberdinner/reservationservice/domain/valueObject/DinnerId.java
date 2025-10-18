package com.buberdinner.reservationservice.domain.valueObject;

import java.util.Objects;

public record DinnerId(Long value) {
    public DinnerId {
        Objects.requireNonNull(value);
    }

    @Override
    public Long value() {
        return value;
    }

    public static DinnerId generate(long value){
        return new DinnerId(value);
    }
}
