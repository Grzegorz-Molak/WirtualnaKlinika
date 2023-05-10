package com.bemsi.controller;

import com.bemsi.model.User;
import com.bemsi.repository.UserLoginRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserLoginController {

    @Autowired
    private UserLoginRepository userLoginRepository;

    @PostMapping
    public String createUser(@RequestBody User userLoginInfo){
        userLoginRepository.save(userLoginInfo);
        return "User added successfully";
    }

    @GetMapping
    public List<User> getAllUser(){
        return userLoginRepository.findAll();
    }
}

