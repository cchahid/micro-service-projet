package com.buberDinner.userService.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;


public record GuestCreated(
        Long id,
        String nom,
        String email
) {
}
