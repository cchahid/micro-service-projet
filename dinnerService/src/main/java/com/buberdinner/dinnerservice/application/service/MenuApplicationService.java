package com.buberdinner.dinnerservice.application.service;



import com.buberdinner.dinnerservice.application.dto.MenuRequest;
import com.buberdinner.dinnerservice.application.dto.MenuResponse;

import java.util.List;

public interface MenuApplicationService {
    MenuResponse createMenu(MenuRequest menuRequest);
    MenuResponse updateMenu(Long id, MenuRequest menuRequest);
    void deleteMenu(Long id);
    MenuResponse getMenuById(Long id);
    //List<MenuResponse> getAllMenus();
    //List<MenuResponse> getMenusByHostId(Long hostId);
    //MenuResponse addDinnerToMenu(Long menuId, Long dinnerId);
    //MenuResponse removeDinnerFromMenu(Long menuId, Long dinnerId);
    //MenuResponse activateMenu(Long id);
   // MenuResponse deactivateMenu(Long id);
}
