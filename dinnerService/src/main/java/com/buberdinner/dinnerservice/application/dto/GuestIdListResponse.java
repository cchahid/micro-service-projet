package com.buberdinner.dinnerservice.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class GuestIdListResponse {
    List<Long> guestIds;
}
