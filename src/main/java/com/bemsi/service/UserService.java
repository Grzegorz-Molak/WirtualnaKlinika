package com.bemsi.service;

import com.bemsi.DTOs.model.UserDetailsDto;
import com.bemsi.DTOs.model.UserDto;

public interface UserService {
    UserDto signUp(UserDetailsDto userDetailsDto);

 //   void logIn();
}
