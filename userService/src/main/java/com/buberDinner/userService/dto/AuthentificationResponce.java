package com.buberDinner.userService.dto;

import com.buberDinner.userService.model.Role;

import lombok.Data;

@Data
public class AuthentificationResponce {
    private String jwt;
    private Long id;
    private Role role;

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
