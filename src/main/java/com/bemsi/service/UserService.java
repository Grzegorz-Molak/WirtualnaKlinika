package com.bemsi.service;

import com.bemsi.DTOs.model.UserDetailsDto;
import com.bemsi.DTOs.model.UserDto;
import org.springframework.http.ResponseEntity;

import java.util.List;


public interface UserService {

    ResponseEntity<String> logIn(UserDto userDto);
    UserDto signUp(UserDetailsDto userDetailsDto);

    //TODO nwm czy usuwać
    UserDetailsDto findUserDetailsByLogin(String login);
    //TODO Usunąć poniższe z prdukcji!!! tylko do testowania
    List<UserDto> findAllUsers();
    List<UserDetailsDto> findAllDetails();

    List<UserDetailsDto> findAllDoctors();
    //   void logIn();
}
