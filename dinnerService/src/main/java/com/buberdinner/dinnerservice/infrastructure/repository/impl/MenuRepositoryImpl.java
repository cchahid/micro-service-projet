package com.buberdinner.dinnerservice.infrastructure.repository.impl;

import com.buberdinner.dinnerservice.domain.entity.Menu;
import com.buberdinner.dinnerservice.domain.repository.MenuRepository;
import com.buberdinner.dinnerservice.domain.valueobject.HostId;
import com.buberdinner.dinnerservice.infrastructure.entity.MenuEntity;
import com.buberdinner.dinnerservice.infrastructure.repository.MenuJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MenuRepositoryImpl implements MenuRepository {
    private final MenuJpaRepository menuJpaRepository;
    @Override
    public Menu createMenu(Menu menu) {
         MenuEntity menuEntity = MenuEntity.fromDomain(menu);
         MenuEntity savedEntity = menuJpaRepository.save(menuEntity);
        return savedEntity.toDomain();

    }

    @Override
    public Menu getMenu(Long menuId) {
         MenuEntity menuEntity = menuJpaRepository.findById(menuId)
                .orElseThrow(() -> new IllegalArgumentException("Menu not found with ID: " + menuId));
         Menu menu = menuEntity.toDomain();
         return menu;
    }

    @Override
    public List<Menu> getHostMenus(HostId hostId) {
        List<MenuEntity> menuEntities = menuJpaRepository.findByHostId(hostId.getValue());
        List<Menu> menus = menuEntities.stream()
                .map(MenuEntity::toDomain)
                .toList();
        return menus;
    }

    @Override
    public Menu updateMenu(Long menuId, Menu menu) {
        MenuEntity menuEntity = MenuEntity.fromDomain(menu);
        MenuEntity savedEntity = menuJpaRepository.save(menuEntity);
        return savedEntity.toDomain();
    }

    @Override
    public void deleteMenu(Long menuId) {
         menuJpaRepository.deleteById(menuId);
    }

    @Override
    public Menu addDinnerToMenu(Long menuId, Long dinnerId) {
        return null;
    }
}
