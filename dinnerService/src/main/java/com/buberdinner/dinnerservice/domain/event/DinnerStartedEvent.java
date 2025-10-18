package com.buberdinner.dinnerservice.domain.event;

import com.buberdinner.dinnerservice.application.dto.DinnerResponse;

import java.util.List;

public record DinnerStartedEvent(
        DinnerResponse dinner,
        List<Long> id_guess
) {
}
