package com.buberdinner.dinnerservice.domain.valueobject;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Value object representing a user ID.
 */
@Getter
@EqualsAndHashCode
@ToString
public class UserId {
    private final Long value;

    private UserId(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        if (value <= 0) {
            throw new IllegalArgumentException("User ID must be positive");
        }
        this.value = value;
    }

    /**
     * Creates a new UserId with the given value.
     *
     * @param value the user ID value
     * @return the UserId value object
     */
    public static UserId of(Long value) {
        return new UserId(value);
    }

    /**
     * Gets the primitive value of this UserId.
     *
     * @return the primitive long value
     */
    public Long getValue() {
        return value;
    }
}