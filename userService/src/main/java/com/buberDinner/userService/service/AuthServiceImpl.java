package com.buberDinner.userService.service;



import com.buberDinner.userService.event.GuestCreated;
import com.buberDinner.userService.event.HostCreated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.buberDinner.userService.dto.SignupRequest;
import com.buberDinner.userService.dto.UserDto;
import com.buberDinner.userService.model.Role;
import com.buberDinner.userService.model.User;
import com.buberDinner.userService.repository.UserRepository;

import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    private final ApplicationEventPublisher publisher;

    @Autowired
    public AuthServiceImpl(UserRepository userRepository, ApplicationEventPublisher publisher) {
        this.userRepository = userRepository;
        this.publisher = publisher;
    }


    @Override
    public UserDto createHost(SignupRequest signupRequest) {
        if(userRepository.findFirstByEmail(signupRequest.getEmail()).isPresent()){
            throw new EntityExistsException("User already exists");
        }
        User user = new User();
        user.setEmail(signupRequest.getEmail());
        user.setNom(signupRequest.getNom());
        user.setMotDePasse(new BCryptPasswordEncoder().encode(signupRequest.getPassword()));
        user.setRole(Role.HOST);
        User userSaved =userRepository.save(user);
        publisher.publishEvent(new HostCreated(user.getId(), user.getEmail(), user.getNom()));
        return userSaved.getUserDto();
    }

    @Override
    public UserDto createGuest(SignupRequest signupRequest) {
       if(userRepository.findFirstByEmail(signupRequest.getEmail()).isPresent()){
            throw new EntityExistsException("User already exists");
        }
        User user = new User();
        user.setEmail(signupRequest.getEmail()); 
        user.setNom(signupRequest.getNom());
        user.setMotDePasse(new BCryptPasswordEncoder().encode(signupRequest.getPassword()));
        user.setRole(Role.GUEST);
        User userSaved =userRepository.save(user);
        publisher.publishEvent(new GuestCreated(user.getId(), user.getNom(),user.getEmail() ));
        return userSaved.getUserDto();
    }






}
