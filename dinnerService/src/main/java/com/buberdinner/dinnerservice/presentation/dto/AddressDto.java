package com.buberdinner.dinnerservice.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for Address information.
 * Used to deserialize address information from JSON.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressDto {
    private String street;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    
    /**
     * Converts this DTO to a formatted address string.
     * Format: "street, city, state, postalCode, country"
     *
     * @return the formatted address string
     */
    public String toFormattedString() {
        return String.format("%s, %s, %s, %s, %s", 
                street, city, state, postalCode, country);
    }
}