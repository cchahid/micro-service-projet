package com.buberdinner.dinnerservice.domain.entity;

import com.buberdinner.dinnerservice.domain.event.EventListner.DinnerEntityListener;
import com.buberdinner.dinnerservice.domain.valueobject.*;

import jakarta.persistence.EntityListeners;

import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEventPublisher;


import java.time.LocalDateTime;
import java.util.ArrayList;

import java.util.List;


/**
 * Domain entity representing a dinner event.
 * This is the aggregate root of the Dinner aggregate.
 */
@EntityListeners(DinnerEntityListener.class)
@Getter
public class Dinner{
    private Long id;

    private HostId hostId;

    private MenuId menuId;

    @Setter
    private String name;

    @Setter
    private String description;

    @Setter
    private double price;

    private TimeRange timeRange;

    private Address address;

    @Setter
    private String cuisineType;

    @Setter
    private int maxGuestCount;

    private DinnerStatus status;

    @Setter
    private String imageUrl;




    private final List<String> errors = new ArrayList<>();


    @Transient
    private static ApplicationEventPublisher publisher;

    /** Méthode appelée par le listener Spring au démarrage */
    public static void setEventPublisher(ApplicationEventPublisher pub) {
        publisher = pub;
    }




    /**
     * Default constructor for JPA.
     */
    protected Dinner() {
        // Required by JPA
    }



    /**
     * Creates a new dinner with the given parameters.
     * The status is automatically set to UPCOMING.
     */
    public Dinner(Long id, Long hostId, Long menuId, String name, String description,
                  double price, LocalDateTime startTime, LocalDateTime endTime,
                  String addressString, String cuisineType,String imageUrl) {
        this(id, hostId, menuId, name, description, price, startTime, endTime, addressString, cuisineType, 0,imageUrl);
        //this.imageUrl=imageUrl;
    }

    /**
     * Creates a new dinner with the given parameters including max guest count.
     * The status is automatically set to UPCOMING.
     */
    public Dinner(Long id, Long hostId, Long menuId, String name, String description,
                  double price, LocalDateTime startTime, LocalDateTime endTime,
                  String addressString, String cuisineType, int maxGuestCount,String imageUrl) {
        this.id = id;

        try {
            this.hostId = HostId.of(hostId);
        } catch (IllegalArgumentException e) {
            errors.add("Invalid host ID: " + e.getMessage());
        }

        try {
            this.menuId = MenuId.of(menuId);
        } catch (IllegalArgumentException e) {
            errors.add("Invalid menu ID: " + e.getMessage());
        }

        this.name = name;
        this.description = description;
        this.price = price;

        try {
            this.timeRange = TimeRange.of(startTime, endTime);
        } catch (IllegalArgumentException e) {
            errors.add("Invalid time range: " + e.getMessage());
        }

        setAddress(addressString);
        this.cuisineType = cuisineType;
        this.maxGuestCount = maxGuestCount;
        this.status = DinnerStatus.UPCOMING;

        this.imageUrl=imageUrl;
        validate();
    }

    /**
     * Sets the address from a formatted string.
     *
     * @param addressString the formatted address string
     */
    public void setAddress(String addressString) {
        try {
            this.address = Address.parse(addressString);
        } catch (IllegalArgumentException e) {
            errors.add("Invalid address format: " + e.getMessage());
        }
    }

    /**
     * Gets the address as a formatted string.
     *
     * @return the formatted address string
     */
    public String getAddress() {
        return address != null ? address.format() : null;
    }

    /**
     * Sets the host ID.
     *
     * @param hostId the host ID
     */
    public void setHostId(Long hostId) {
        try {
            this.hostId = HostId.of(hostId);
            validate();
        } catch (IllegalArgumentException e) {
            errors.add("Invalid host ID: " + e.getMessage());
        }
    }

    /**
     * Gets the host ID as a primitive long.
     *
     * @return the host ID value
     */
    public Long getHostId() {
        return hostId != null ? hostId.getValue() : null;
    }

    /**
     * Sets the menu ID.
     *
     * @param menuId the menu ID
     */
    public void setMenuId(Long menuId) {
        try {
            this.menuId = MenuId.of(menuId);
            validate();
        } catch (IllegalArgumentException e) {
            errors.add("Invalid menu ID: " + e.getMessage());
        }
    }

    /**
     * Gets the menu ID as a primitive long.
     *
     * @return the menu ID value
     */
    public Long getMenuId() {
        return menuId != null ? menuId.getValue() : null;
    }

    /**
     * Sets the start time of the dinner.
     *
     * @param startTime the start time
     */
    public void setStartTime(LocalDateTime startTime) {
        LocalDateTime endTime = getEndTime();
        try {
            this.timeRange = TimeRange.of(startTime, endTime != null ? endTime : startTime.plusHours(3));
            validate();
        } catch (IllegalArgumentException e) {
            errors.add("Invalid start time: " + e.getMessage());
        }
    }

    /**
     * Gets the start time.
     *
     * @return the start time
     */
    public LocalDateTime getStartTime() {
        return timeRange != null ? timeRange.getStartTime() : null;
    }

    /**
     * Sets the end time of the dinner.
     *
     * @param endTime the end time
     */
    public void setEndTime(LocalDateTime endTime) {
        LocalDateTime startTime = getStartTime();
        try {
            this.timeRange = TimeRange.of(startTime != null ? startTime : endTime.minusHours(3), endTime);
            validate();
        } catch (IllegalArgumentException e) {
            errors.add("Invalid end time: " + e.getMessage());
        }
    }

    /**
     * Gets the end time.
     *
     * @return the end time
     */
    public LocalDateTime getEndTime() {
        return timeRange != null ? timeRange.getEndTime() : null;
    }

    /**
     * Validates the dinner entity.
     * Adds validation errors to the errors list.
     */
    private void validate() {
        errors.clear();

        if (hostId == null) {
            errors.add("Host ID is required");
        }

        if (menuId == null) {
            errors.add("Menu ID is required");
        }

        if (name == null || name.trim().isEmpty()) {
            errors.add("Name is required");
        }

        if (price < 0) {
            errors.add("Price cannot be negative");
        }

        if (timeRange == null) {
            errors.add("Time range is required");
        }

        if (address == null) {
            errors.add("Address is required");
        }

        if (cuisineType == null || cuisineType.trim().isEmpty()) {
            errors.add("Cuisine type is required");
        }

        if (maxGuestCount < 0) {
            errors.add("Maximum guest count cannot be negative");
        }
    }

    /**
     * Checks if the dinner is valid.
     *
     * @return true if the dinner is valid, false otherwise
     */
    public boolean isValid() {
        return errors.isEmpty();
    }

    /**
     * Gets the validation errors.
     *
     * @return the list of validation errors
     */
    public List<String> getErrors() {
        return new ArrayList<>(errors);
    }



    /**
     * Reschedules the dinner to a new time.
     *
     * @param newStartTime the new start time
     * @param newEndTime   the new end time
     * @return true if the dinner was rescheduled, false otherwise
     */
    public boolean reschedule(LocalDateTime newStartTime, LocalDateTime newEndTime) {
        if (status == DinnerStatus.COMPLETED) {
            errors.add("Cannot reschedule a completed or cancelled dinner");
            return false;
        }

        if (newStartTime == null || newEndTime == null) {
            errors.add("New start and end times are required");
            return false;
        }

        try {
            this.timeRange = TimeRange.of(newStartTime, newEndTime);
            this.status = DinnerStatus.RESCHEDULED;
            return true;
        } catch (IllegalArgumentException e) {
            errors.add("Invalid time range: " + e.getMessage());
            return false;
        }
    }



    /**
     * Marks the dinner as in progress.
     *
     * @return true if the dinner was marked as in progress, false otherwise
     */

    public boolean start() {
        if (status != DinnerStatus.UPCOMING && status != DinnerStatus.RESCHEDULED) {
            errors.add("Only upcoming or rescheduled dinners can be started");
            return false;
        }
        this.status = DinnerStatus.IN_PROGRESS;
        //this.domainEvents.add(new DinnerStartedEvent(this.getId()));
        return true;
    }

    public boolean complete() {
        if (status != DinnerStatus.IN_PROGRESS) {
            errors.add("Only in-progress dinners can be completed");
            return false;
        }
        this.status = DinnerStatus.COMPLETED;
        //this.domainEvents.add(new DinnerCompletedEvent(this.getId()));
        return true;
    }





}