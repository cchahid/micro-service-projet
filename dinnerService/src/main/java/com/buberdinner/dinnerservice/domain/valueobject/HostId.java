package com.buberdinner.dinnerservice.domain.valueobject;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Value object representing a host ID.
 */
@Getter
@EqualsAndHashCode
@ToString
public class HostId {
    private final Long value;

    private HostId(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("Host ID cannot be null");
        }
        if (value <= 0) {
            throw new IllegalArgumentException("Host ID must be positive");
        }
        this.value = value;
    }

    /**
     * Creates a new HostId with the given value.
     *
     * @param value the host ID value
     * @return the HostId value object
     */
    public static HostId of(Long value) {
        return new HostId(value);
    }

    /**
     * Gets the primitive value of this HostId.
     *
     * @return the primitive long value
     */
    public Long getValue() {
        return value;
    }
}