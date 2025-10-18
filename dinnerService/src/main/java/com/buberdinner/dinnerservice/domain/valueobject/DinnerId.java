package com.buberdinner.dinnerservice.domain.valueobject;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Value object representing a dinner ID.
 */
@Getter
@EqualsAndHashCode
@ToString
public class DinnerId {
    private final Long value;

    private DinnerId(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("Dinner ID cannot be null");
        }
        if (value <= 0) {
            throw new IllegalArgumentException("Dinner ID must be positive");
        }
        this.value = value;
    }

    /**
     * Creates a new DinnerId with the given value.
     *
     * @param value the dinner ID value
     * @return the DinnerId value object
     */
    public static DinnerId of(Long value) {
        return new DinnerId(value);
    }

    /**
     * Gets the primitive value of this DinnerId.
     *
     * @return the primitive long value
     */
    public Long getValue() {
        return value;
    }
}