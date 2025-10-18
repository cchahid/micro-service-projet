package com.buberdinner.dinnerservice.domain.exception;

public class DinnerNotFoundException extends RuntimeException {
    public DinnerNotFoundException(Long id) {
        super("Dinner not found with id: " + id);
    }
}