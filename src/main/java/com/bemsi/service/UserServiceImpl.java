package com.bemsi.service;

import com.bemsi.repository.UserDetailsRepository;
import com.bemsi.repository.UserLoginRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserLoginRepository userLoginRepository;

    @Autowired
    private UserDetailsRepository userDetailsRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;
}
