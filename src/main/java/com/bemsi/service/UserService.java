package com.bemsi.service;

import com.bemsi.DTOs.model.UserDetailsDto;
import com.bemsi.DTOs.model.UserDto;

import java.util.List;

public interface UserService {
    UserDto signUp(UserDetailsDto userDetailsDto);

    //TODO Usunąć poniższe z prdukcji!!! tylko do testowania
    List<UserDto> findAllUsers();
    List<UserDetailsDto> findAllDetails();

    List<UserDetailsDto> findAllDoctors();
    //   void logIn();
}
