package com.buberdinner.dinnerservice.application.service;

import com.buberdinner.dinnerservice.application.dto.DinnerRequest;
import com.buberdinner.dinnerservice.application.dto.DinnerResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface DinnerApplicationService {
    DinnerResponse createDinner(DinnerRequest dinnerRequest, String imageUrl);
    DinnerResponse updateDinner(Long id, DinnerRequest dinnerRequest);
    void deleteDinner(Long id);
    DinnerResponse getDinnerById(Long id);
    List<DinnerResponse> getAllDinners();
    List<DinnerResponse> getDinnersByHostId(Long hostId);
    List<DinnerResponse> getDinnersByMenuId(Long menuId);
    boolean RescheduleDinner(long dinnerId, LocalDateTime newStart, LocalDateTime newEnd, List<String> errors);
    void startDinner(Long dinnerId);
    void completeDinner(Long dinnerId);
    void startAllDinnersInMenu(Long menuId);

}