package com.bemsi.controller;

import com.bemsi.model.User;
import com.bemsi.model.UserDetails;
import com.bemsi.repository.UserDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/details")
public class UserDetailsController {

    @Autowired
    private UserDetailsRepository userDetailsRepository;

    @GetMapping
    public List<UserDetails> getAllUserDetails(){
        return userDetailsRepository.findAll();
    }
}
