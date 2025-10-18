package com.buberdinner.dinnerservice.application.service.impl;


import com.buberdinner.dinnerservice.application.dto.MenuRequest;
import com.buberdinner.dinnerservice.application.dto.MenuResponse;
import com.buberdinner.dinnerservice.application.service.MenuApplicationService;
import com.buberdinner.dinnerservice.domain.entity.Menu;
//import com.buberdinner.dinnerservice.domain.exception.MenuNotFoundException;
import com.buberdinner.dinnerservice.domain.exception.MenuNotFoundException;
import com.buberdinner.dinnerservice.domain.repository.MenuRepository;
import com.buberdinner.dinnerservice.domain.valueobject.HostId;
import com.buberdinner.dinnerservice.domain.valueobject.MenuStatus;
import com.buberdinner.dinnerservice.infrastructure.client.UserServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MenuApplicationServiceImpl implements MenuApplicationService {

    private final MenuRepository menuRepository;
    private final UserServiceClient userServiceClient;

    @Override
    @Transactional
    public MenuResponse createMenu(MenuRequest menuRequest) {
        // Validate host ID
        if (!userServiceClient.isUserHost(menuRequest.hostId())) {
            throw new IllegalArgumentException("Invalid host ID: User does not exist or is not a host");
        }

        Menu menu = mapToMenu(menuRequest);

        if (!menu.isValid()) {
            throw new IllegalArgumentException("Invalid menu: " + String.join(", ", menu.getErrors()));
        }

        Menu savedMenu = menuRepository.createMenu(menu);
        log.info("Saved menu: {}", savedMenu);
        return mapToMenuResponse(savedMenu);
    }

    @Override
    @Transactional
    public MenuResponse updateMenu(Long id, MenuRequest menuRequest) {
        Menu existingMenu = menuRepository.getMenu(id);

        if (existingMenu == null) {
            throw new MenuNotFoundException("menu not found! "+id);
        }

        // Validate host ID
        if (!userServiceClient.isUserHost(menuRequest.hostId())) {
            throw new IllegalArgumentException("Invalid host ID: User does not exist or is not a host");
        }

        updateMenuFromRequest(existingMenu, menuRequest);

        if (!existingMenu.isValid()) {
            throw new IllegalArgumentException("Invalid menu: " + String.join(", ", existingMenu.getErrors()));
        }

        Menu updatedMenu = menuRepository.createMenu(existingMenu);
        return mapToMenuResponse(updatedMenu);
    }

    @Override
    @Transactional
    public void deleteMenu(Long id) {
        Menu menu = menuRepository.getMenu(id);
        if (menu == null) {
            throw new MenuNotFoundException("menu not found! "+id);
        }


        menuRepository.deleteMenu(menu.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public MenuResponse getMenuById(Long id) {
        Menu menu = menuRepository.getMenu(id);
        if (menu == null) {
            throw new MenuNotFoundException("menu not found! "+id);
        }
        return mapToMenuResponse(menu);
    }

    /*@Override
    @Transactional(readOnly = true)
    public List<MenuResponse> getAllMenus() {
        return menuRepository..stream()
                .map(this::mapToMenuResponse)
                .collect(Collectors.toList());
    }*/

    /*@Override
    @Transactional(readOnly = true)
    public List<MenuResponse> getMenusByHostId(Long hostId) {
        return menuRepository.getHostMenus().stream()
                .map(this::mapToMenuResponse)
                .collect(Collectors.toList());
    }*/

    /*@Override
    @Transactional
    public MenuResponse addDinnerToMenu(Long menuId, Long dinnerId) {
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new MenuNotFoundException(menuId));

        // Ici vous devrez récupérer le Dinner via un service ou repository
        // Dinner dinner = dinnerService.getDinnerById(dinnerId);

        // Vérifier que le dinner appartient bien au même host
        // if (!dinner.getHostId().equals(menu.getHostId())) {
        //     throw new IllegalArgumentException("Dinner does not belong to menu's host");
        // }

        // menu.addDinner(dinner);
        Menu updatedMenu = menuRepository.save(menu);
        return mapToMenuResponse(updatedMenu);
    }*/

   /* @Override
    @Transactional
    public MenuResponse removeDinnerFromMenu(Long menuId, Long dinnerId) {
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new MenuNotFoundException(menuId));

        // menu.removeDinner(dinnerId);
        Menu updatedMenu = menuRepository.save(menu);
        return mapToMenuResponse(updatedMenu);
    }*/

   /* @Override
    @Transactional
    public MenuResponse activateMenu(Long id) {
        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> new MenuNotFoundException(id));
        menu.activate();
        return mapToMenuResponse(menuRepository.save(menu));
    }

    @Override
    @Transactional
    public MenuResponse deactivateMenu(Long id) {
        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> new MenuNotFoundException(id));
        menu.deactivate();
        return mapToMenuResponse(menuRepository.save(menu));
    }*/

    private Menu mapToMenu(MenuRequest menuRequest) {
        return new Menu(
                null,
                menuRequest.hostId(),
                menuRequest.name(),
                menuRequest.description(),
                menuRequest.cuisineType(),
                menuRequest.isActive()? MenuStatus.ACTIVE:MenuStatus.INACTIVE
        );
    }

    private void updateMenuFromRequest(Menu menu, MenuRequest menuRequest) {
        menu.setName(menuRequest.name());
        menu.setDescription(menuRequest.description());
        menu.setCuisineType(menuRequest.cuisineType());
        if (menuRequest.isActive()) {
            menu.activate();
        } else {
            menu.deactivate();
        }
    }

    private MenuResponse mapToMenuResponse(Menu menu) {
        return new MenuResponse(
                menu.getId(),
                menu.getHostId().getValue(),
                menu.getName(),
                menu.getDescription(),
                menu.getCuisineType()// Supposant que vous avez cette méthode
        );
    }
}
