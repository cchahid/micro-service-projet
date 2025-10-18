package com.buberdinner.dinnerservice.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RescheduleDinnerRequest {
    public LocalDateTime newStartTime;
    public LocalDateTime newEndTime;
}
