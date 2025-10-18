package com.buberDinner.userService.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.buberDinner.userService.dto.AuthentificationRequest;
import com.buberDinner.userService.dto.AuthentificationResponce;
import com.buberDinner.userService.dto.SignupRequest;
import com.buberDinner.userService.dto.UserDto;
import com.buberDinner.userService.jwt.JwtUtil;
import com.buberDinner.userService.model.User;
import com.buberDinner.userService.repository.UserRepository;
import com.buberDinner.userService.service.AuthService;
import com.buberDinner.userService.service.UserService;

import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    @Autowired
    private UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final UserService userService;

    @Autowired
    public AuthController(AuthService authService, AuthenticationManager authenticationManager, 
                         JwtUtil jwtUtil, UserService userService) {
        this.authService = authService;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }
    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("pong");
    }
    @PostMapping("/signUp/Guest")
    public ResponseEntity<?> signupGuest(@RequestBody SignupRequest signupRequest) {
       try{
        UserDto createUser = authService.createGuest(signupRequest);
        return new ResponseEntity<>(createUser, HttpStatus.OK);
       }catch(EntityExistsException e){
        return new ResponseEntity<>("User already exists", HttpStatus.NOT_ACCEPTABLE);
       }catch(Exception e){
        return new ResponseEntity<>("User creation failed", HttpStatus.BAD_REQUEST);
       }
    }  


    @PostMapping("/signUp/Host")
    public ResponseEntity<?> signupHost(@RequestBody SignupRequest signupRequest) {
       try{
        UserDto createUser = authService.createHost(signupRequest);
        return new ResponseEntity<>(createUser, HttpStatus.OK);
       }catch(EntityExistsException e){
        return new ResponseEntity<>("User already exists", HttpStatus.NOT_ACCEPTABLE);
       }catch(Exception e){
        return new ResponseEntity<>("User creation failed", HttpStatus.BAD_REQUEST);
       }
    }  

    @PostMapping("/login")
    public AuthentificationResponce authenticateUser(@RequestBody AuthentificationRequest authentificationRequest) {
          try{
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authentificationRequest.getEmail(), authentificationRequest.getPassword()));
          }catch(BadCredentialsException e){
            throw new BadCredentialsException("Invalid credentials"); 
          }
          final UserDetails userDetalis = userService.userDetailsService().loadUserByUsername(authentificationRequest.getEmail());
          Optional<User> user = userRepository.findFirstByEmail(userDetalis.getUsername());
          final String jwt = jwtUtil.generateToken(userDetalis);
          AuthentificationResponce authentificationResponce = new AuthentificationResponce();
          if(user.isPresent()){
            authentificationResponce.setId(user.get().getId());
            authentificationResponce.setRole(user.get().getRole());
            authentificationResponce.setJwt(jwt);
          }
          return authentificationResponce;

    } 



}
