package com.buberdinner.dinnerservice.domain.valueobject;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * Value object representing an address.
 */
@Getter
@AllArgsConstructor
@ToString
public class Address {
    private final String street;
    private final String city;
    private final String state;
    private final String postalCode;
    private final String country;

    /**
     * Creates a formatted string representation of the address.
     * Format: "street, city, state, postalCode, country"
     *
     * @return the formatted address string
     */
    public String format() {
        return String.format("%s, %s, %s, %s, %s", 
                street, city, state, postalCode, country);
    }

    /**
     * Parses a formatted address string into an Address object.
     * Expected format: "street, city, state, postalCode, country"
     *
     * @param addressString the formatted address string
     * @return the Address object
     * @throws IllegalArgumentException if the address format is invalid
     */
    public static Address parse(String addressString) {
        if (addressString == null || addressString.trim().isEmpty()) {
            throw new IllegalArgumentException("Address cannot be null or empty");
        }

        String[] parts = addressString.split(",");
        if (parts.length != 5) {
            throw new IllegalArgumentException("Invalid address format. Expected: street, city, state, postalCode, country");
        }

        String street = parts[0].trim();
        String city = parts[1].trim();
        String state = parts[2].trim();
        String postalCode = parts[3].trim();
        String country = parts[4].trim();

        // Validate each part
        if (street.isEmpty() || city.isEmpty() || state.isEmpty() || postalCode.isEmpty() || country.isEmpty()) {
            throw new IllegalArgumentException("All address components must be non-empty");
        }

        return new Address(street, city, state, postalCode, country);
    }

    /**
     * Validates if the address string is in the correct format.
     *
     * @param addressString the address string to validate
     * @return true if the address is valid, false otherwise
     */
    public static boolean isValid(String addressString) {
        try {
            parse(addressString);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}