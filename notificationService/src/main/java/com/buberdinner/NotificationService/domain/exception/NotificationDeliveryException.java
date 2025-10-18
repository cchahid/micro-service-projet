package com.buberdinner.NotificationService.domain.exception;

public class NotificationDeliveryException extends RuntimeException {
    public NotificationDeliveryException(String message , Throwable cause) {
        super(message , cause);
    }
}