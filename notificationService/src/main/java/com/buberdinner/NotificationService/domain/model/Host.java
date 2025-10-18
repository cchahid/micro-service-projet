package com.buberdinner.NotificationService.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Host {
    private Long id;
    private String name;
    private String email;
}