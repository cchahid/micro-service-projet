package com.buberDinner.userService.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.buberDinner.userService.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
public class UserServiceImpl implements UserService {
   private final UserRepository userRepository;

   @Autowired
   public UserServiceImpl(UserRepository userRepository) {
       this.userRepository = userRepository;
   }

  public UserDetailsService userDetailsService() {
   return new UserDetailsService() {
        @Override
        public UserDetails loadUserByUsername(String username) {
        return userRepository.findFirstByEmail(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
   }
  };
}

}
