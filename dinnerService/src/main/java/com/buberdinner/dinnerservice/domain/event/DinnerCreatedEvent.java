package com.buberdinner.dinnerservice.domain.event;

import com.buberdinner.dinnerservice.presentation.dto.DinnerResponse;

public record DinnerCreatedEvent(
        DinnerResponse dinner
) {
}
