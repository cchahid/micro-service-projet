package com.buberDinner.userService.dto;

import com.buberDinner.userService.model.Role;
import com.buberDinner.userService.model.User;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class UserDto {

    private Long id;
    private String email;
    private String nom;
    private Role role;
    private String prenom;

    public UserDto(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.nom = user.getNom();
        this.role = user.getRole();
        this.prenom = user.getPrenom();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }
}
