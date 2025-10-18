package com.buberdinner.dinnerservice.domain.valueobject;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.time.Duration;

/**
 * Value object representing a time range with start and end times.
 */
@Getter
@EqualsAndHashCode
@ToString
public class TimeRange {
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;

    private TimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime == null) {
            throw new IllegalArgumentException("Start time cannot be null");
        }
        if (endTime == null) {
            throw new IllegalArgumentException("End time cannot be null");
        }
        if (!startTime.isBefore(endTime)) {
            throw new IllegalArgumentException("Start time must be before end time");
        }
        this.startTime = startTime;
        this.endTime = endTime;
    }

    /**
     * Creates a new TimeRange with the given start and end times.
     *
     * @param startTime the start time
     * @param endTime the end time
     * @return the TimeRange value object
     */
    public static TimeRange of(LocalDateTime startTime, LocalDateTime endTime) {
        return new TimeRange(startTime, endTime);
    }

    /**
     * Checks if the given time is within this time range.
     *
     * @param time the time to check
     * @return true if the time is within the range, false otherwise
     */
    public boolean contains(LocalDateTime time) {
        return (time.isEqual(startTime) || time.isAfter(startTime)) && 
               (time.isEqual(endTime) || time.isBefore(endTime));
    }

    /**
     * Gets the duration of this time range.
     *
     * @return the duration between start and end times
     */
    public Duration getDuration() {
        return Duration.between(startTime, endTime);
    }

    /**
     * Checks if this time range overlaps with another time range.
     *
     * @param other the other time range
     * @return true if the ranges overlap, false otherwise
     */
    public boolean overlaps(TimeRange other) {
        return (this.startTime.isBefore(other.endTime) || this.startTime.isEqual(other.endTime)) && 
               (this.endTime.isAfter(other.startTime) || this.endTime.isEqual(other.startTime));
    }
}