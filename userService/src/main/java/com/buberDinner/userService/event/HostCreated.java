package com.buberDinner.userService.event;

public record HostCreated(
        Long id,
        String email,
        String nom
) {
}
