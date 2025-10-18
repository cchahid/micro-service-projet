package com.buberDinner.userService.service;

import com.buberDinner.userService.dto.SignupRequest;
import com.buberDinner.userService.dto.UserDto;

public interface AuthService {
       
     UserDto createHost(SignupRequest signupRequest);
     UserDto createGuest(SignupRequest signupRequest);

}
