package com.buberdinner.dinnerservice.domain.valueobject;

/**
 * Represents the status of a dinner.
 */
public enum DinnerStatus {
    /**
     * The dinner is upcoming and has not started yet.
     */
    UPCOMING,
    
    /**
     * The dinner is currently in progress.
     */
    IN_PROGRESS,
    
    /**
     * The dinner has been completed.
     */
    COMPLETED,

    
    /**
     * The dinner has been rescheduled.
     */
    RESCHEDULED
}