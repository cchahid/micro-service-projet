package com.buberdinner.dinnerservice.presentation.mapper;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class DinnerDtoMapper {

    public com.buberdinner.dinnerservice.application.dto.DinnerRequest toApplicationRequest(
            com.buberdinner.dinnerservice.presentation.dto.DinnerRequest presentationRequest) {
        // Get the address as a formatted string
        String formattedAddress = presentationRequest.getAddress();

        return new com.buberdinner.dinnerservice.application.dto.DinnerRequest(
                presentationRequest.getHostId(),
                presentationRequest.getMenuId(),
                presentationRequest.getName(),
                presentationRequest.getDescription(),
                presentationRequest.getPrice(),
                presentationRequest.getStartTime(),
                presentationRequest.getEndTime(),
                formattedAddress, // Use the formatted address
                presentationRequest.getCuisineType(),
                presentationRequest.getMaxGuestCount()
        );
    }

    public com.buberdinner.dinnerservice.presentation.dto.DinnerResponse toPresentationResponse(
            com.buberdinner.dinnerservice.application.dto.DinnerResponse applicationResponse) {
        return new com.buberdinner.dinnerservice.presentation.dto.DinnerResponse(
                applicationResponse.getId(),
                applicationResponse.getHostId(),
                applicationResponse.getMenuId(),
                applicationResponse.getName(),
                applicationResponse.getDescription(),
                applicationResponse.getPrice(),
                applicationResponse.getStartTime(),
                applicationResponse.getEndTime(),
                applicationResponse.getImageUrl(),
                applicationResponse.getAddress(),
                applicationResponse.getCuisineType(),
                applicationResponse.getMaxGuestCount(),
                applicationResponse.getStatus(),
                applicationResponse.getRating()

        );
    }

    public List<com.buberdinner.dinnerservice.presentation.dto.DinnerResponse> toPresentationResponseList(
            List<com.buberdinner.dinnerservice.application.dto.DinnerResponse> applicationResponses) {
        return applicationResponses.stream()
                .map(this::toPresentationResponse)
                .collect(Collectors.toList());
    }
}
