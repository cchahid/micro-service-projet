package com.buberDinner.userService.repository;



import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.buberDinner.userService.model.Role;
import com.buberDinner.userService.model.User;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Ajoutez des méthodes spécifiques si nécessaire
    Optional<User> findFirstByEmail(String email);
    Optional<List<User>> findAllByRole(Role role);
    Optional<User> findByRole(Role role);
    Optional<User> findById(Long id);
}
