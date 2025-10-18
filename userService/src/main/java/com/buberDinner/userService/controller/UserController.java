package com.buberDinner.userService.controller;

import com.buberDinner.userService.model.Role;
import com.buberDinner.userService.model.User;
import com.buberDinner.userService.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserRepository userRepository;

    @Autowired
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> checkUserExists(@PathVariable Long id) {
        return ResponseEntity.ok(userRepository.findById(id).isPresent());
    }

    @GetMapping("/{id}/isHost")
    public ResponseEntity<Boolean> checkUserIsHost(@PathVariable Long id) {
        Optional<User> user = userRepository.findById(id);
        return ResponseEntity.ok(user.isPresent() && user.get().getRole() == Role.HOST);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(user -> ResponseEntity.ok(user.getUserDto()))
                .orElse(ResponseEntity.notFound().build());
    }
}
