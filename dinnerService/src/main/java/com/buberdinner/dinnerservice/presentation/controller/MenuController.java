package com.buberdinner.dinnerservice.presentation.controller;

import com.buberdinner.dinnerservice.application.service.MenuApplicationService;
import com.buberdinner.dinnerservice.presentation.dto.MenuRequest;
import com.buberdinner.dinnerservice.presentation.dto.MenuResponse;
import com.buberdinner.dinnerservice.presentation.mapper.MenuDtoMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/menus")
public class MenuController {

    private final MenuApplicationService menuApplicationService;

    private final MenuDtoMapper menuDtoMapper;

    public MenuController(MenuApplicationService menuApplicationService, MenuDtoMapper menuDtoMapper) {
        this.menuApplicationService = menuApplicationService;
        this.menuDtoMapper = menuDtoMapper;
    }


    @PostMapping
    public ResponseEntity<MenuResponse> createMenu(@RequestBody MenuRequest menuRequest) {
        var applicationRequest = menuDtoMapper.toApplicationRequest(menuRequest);
        var applicationResponse = menuApplicationService.createMenu(applicationRequest);
        var presentationResponse = menuDtoMapper.toPresentationResponse(applicationResponse);
        return new ResponseEntity<>(presentationResponse, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MenuResponse> getMenuById(@PathVariable Long id) {
        var applicationResponse = menuApplicationService.getMenuById(id);
        var presentationResponse = menuDtoMapper.toPresentationResponse(applicationResponse);
        return ResponseEntity.ok(presentationResponse);
    }

    /*@GetMapping
    public ResponseEntity<List<MenuResponse>> getAllMenus() {
        var applicationResponses = menuApplicationService.getAllMenus();
        var presentationResponses = menuDtoMapper.toPresentationResponseList(applicationResponses);
        return ResponseEntity.ok(presentationResponses);
    }*/

    /*@GetMapping("/host/{hostId}")
    public ResponseEntity<List<MenuResponse>> getMenusByHostId(@PathVariable Long hostId) {
        var applicationResponses = menuApplicationService.getMenusByHostId(hostId);
        var presentationResponses = menuDtoMapper.toPresentationResponseList(applicationResponses);
        return ResponseEntity.ok(presentationResponses);
    }*/

    @PutMapping("/{id}")
    public ResponseEntity<MenuResponse> updateMenu(
            @PathVariable Long id,
            @RequestBody MenuRequest menuRequest) {
        var applicationRequest = menuDtoMapper.toApplicationRequest(menuRequest);
        var applicationResponse = menuApplicationService.updateMenu(id, applicationRequest);
        var presentationResponse = menuDtoMapper.toPresentationResponse(applicationResponse);
        return ResponseEntity.ok(presentationResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMenu(@PathVariable Long id) {
        menuApplicationService.deleteMenu(id);
        return ResponseEntity.noContent().build();
    }





    /*@PostMapping("/{id}/activate")
    public ResponseEntity<MenuResponse> activateMenu(@PathVariable Long id) {
        var applicationResponse = menuApplicationService.activateMenu(id);
        var presentationResponse = menuDtoMapper.toPresentationResponse(applicationResponse);
        return ResponseEntity.ok(presentationResponse);
    }*/


}

