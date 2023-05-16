package com.bemsi.service;

import com.bemsi.DTOs.model.UserDetailsDto;
import com.bemsi.DTOs.model.UserDto;
import com.bemsi.model.User;
import com.bemsi.security.PasswordEncoder;

import java.util.List;
import java.util.Optional;

public interface UserService {


    UserDto signUp(UserDetailsDto userDetailsDto);

    //TODO nwm czy usuwać
    UserDetailsDto findUserDetailsByLogin(String login);
    //TODO Usunąć poniższe z prdukcji!!! tylko do testowania
    List<UserDto> findAllUsers();
    List<UserDetailsDto> findAllDetails();

    List<UserDetailsDto> findAllDoctors();
    //   void logIn();
}
