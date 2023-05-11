package com.bemsi.controller;

import com.bemsi.DTOs.model.UserDetailsDto;
import com.bemsi.DTOs.model.UserDto;
import com.bemsi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    @Autowired
    public UserController(UserService userService){
        this.userService = userService;
    }

    @PostMapping("/signup")
    private UserDto signUp(@RequestBody UserDetailsDto userDetailsDto){
        return userService.signUp(userDetailsDto);
    }

    @PostMapping("/login")
    private String logIn(@RequestBody UserDto userDto){

        //userService.logIn();
        return null;
    }


}

