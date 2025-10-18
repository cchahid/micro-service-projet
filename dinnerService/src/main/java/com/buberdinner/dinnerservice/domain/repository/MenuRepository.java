package com.buberdinner.dinnerservice.domain.repository;

import com.buberdinner.dinnerservice.domain.entity.Menu;
import com.buberdinner.dinnerservice.domain.valueobject.HostId;
import com.buberdinner.dinnerservice.domain.valueobject.MenuId;

import java.util.List;
import java.util.Optional;

public interface MenuRepository {
    Menu createMenu(Menu menu);
    Menu getMenu(Long menuId);
    List<Menu> getHostMenus(HostId hostId);
    Menu updateMenu(Long menuId, Menu menu);
    void deleteMenu(Long menuId);
    Menu addDinnerToMenu(Long menuId, Long dinnerId);
}