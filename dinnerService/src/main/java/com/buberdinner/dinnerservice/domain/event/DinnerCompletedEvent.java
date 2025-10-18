package com.buberdinner.dinnerservice.domain.event;

public record DinnerCompletedEvent(
        Long dinnerId
) {
}
