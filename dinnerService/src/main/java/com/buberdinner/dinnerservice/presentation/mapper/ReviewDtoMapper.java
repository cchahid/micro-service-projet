package com.buberdinner.dinnerservice.presentation.mapper;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ReviewDtoMapper {

    public com.buberdinner.dinnerservice.application.dto.ReviewRequest toApplicationRequest(
            com.buberdinner.dinnerservice.presentation.dto.ReviewRequest presentationRequest) {
        return new com.buberdinner.dinnerservice.application.dto.ReviewRequest(
                presentationRequest.getHostId(),
                presentationRequest.getUserId(),
                presentationRequest.getComment(),
                presentationRequest.getRating()
        );
    }

    public com.buberdinner.dinnerservice.presentation.dto.ReviewResponse toPresentationResponse(
            com.buberdinner.dinnerservice.application.dto.ReviewResponse applicationResponse) {
        return new com.buberdinner.dinnerservice.presentation.dto.ReviewResponse(
                applicationResponse.getId(),
                applicationResponse.getDinnerId(),
                applicationResponse.getUserId(),
                applicationResponse.getComment(),
                applicationResponse.getRating(),
                applicationResponse.getCreatedAt()
        );
    }

    public List<com.buberdinner.dinnerservice.presentation.dto.ReviewResponse> toPresentationResponseList(
            List<com.buberdinner.dinnerservice.application.dto.ReviewResponse> applicationResponses) {
        return applicationResponses.stream()
                .map(this::toPresentationResponse)
                .collect(Collectors.toList());
    }
}