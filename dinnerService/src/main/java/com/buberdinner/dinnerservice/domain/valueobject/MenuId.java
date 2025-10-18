package com.buberdinner.dinnerservice.domain.valueobject;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Value object representing a menu ID.
 */
@Getter
@EqualsAndHashCode
@ToString
public class MenuId {
    private final Long value;

    private MenuId(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("Menu ID cannot be null");
        }
        if (value <= 0) {
            throw new IllegalArgumentException("Menu ID must be positive");
        }
        this.value = value;
    }

    /**
     * Creates a new MenuId with the given value.
     *
     * @param value the menu ID value
     * @return the MenuId value object
     */
    public static MenuId of(Long value) {
        return new MenuId(value);
    }

    /**
     * Gets the primitive value of this MenuId.
     *
     * @return the primitive long value
     */
    public Long getValue() {
        return value;
    }
}