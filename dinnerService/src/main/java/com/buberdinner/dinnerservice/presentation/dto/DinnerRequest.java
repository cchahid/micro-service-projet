package com.buberdinner.dinnerservice.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DinnerRequest {
    private Long hostId;
    private Long menuId;
    private String name;
    private String description;
    private double price;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    // Can be either a String or an AddressDto object
    private String addressString;
    private AddressDto addressDto;

    private String cuisineType;

    private int maxGuestCount;

    /**
     * Gets the address as a formatted string.
     * If addressDto is provided, it converts it to a string.
     * Otherwise, returns the addressString.
     * 
     * @return the formatted address string
     */
    @JsonIgnore
    public String getAddress() {
        if (addressDto != null) {
            return addressDto.toFormattedString();
        }
        return addressString;
    }

    /**
     * Sets the address string.
     * 
     * @param address the address string
     */
    @JsonProperty("address")
    public void setAddress(String address) {
        this.addressString = address;
    }
}
