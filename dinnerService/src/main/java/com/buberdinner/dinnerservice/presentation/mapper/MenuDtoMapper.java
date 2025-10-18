package com.buberdinner.dinnerservice.presentation.mapper;


import com.buberdinner.dinnerservice.application.dto.DinnerRequest;
import com.buberdinner.dinnerservice.application.dto.MenuResponse;
import com.buberdinner.dinnerservice.application.service.DinnerApplicationService;
import com.buberdinner.dinnerservice.presentation.dto.DinnerUpdateRequest;
import com.buberdinner.dinnerservice.presentation.dto.MenuRequest;
import org.springframework.stereotype.Component;

@Component
public class MenuDtoMapper {



    public com.buberdinner.dinnerservice.application.dto.MenuRequest toApplicationRequest(MenuRequest menuRequest) {
        return new com.buberdinner.dinnerservice.application.dto.MenuRequest(
                menuRequest.hostId(),
                menuRequest.name(),
                menuRequest.description()
                ,menuRequest.cuisineType(),
                menuRequest.isActive()
        );
    }



    public com.buberdinner.dinnerservice.presentation.dto.MenuResponse toPresentationResponse(MenuResponse applicationResponse) {
          return new com.buberdinner.dinnerservice.presentation.dto.MenuResponse(
                  applicationResponse.id(),
                  applicationResponse.hostId(),
                  applicationResponse.name(),
                  applicationResponse.description(),
                  applicationResponse.cuisineType(),
                  applicationResponse.isActive()
          );
    }
}
