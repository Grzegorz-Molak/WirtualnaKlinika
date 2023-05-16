package com.bemsi.service;

import com.bemsi.DTOs.mapper.UserMapper;
import com.bemsi.DTOs.model.UserDetailsDto;
import com.bemsi.DTOs.model.UserDto;
import com.bemsi.model.Specialization;
import com.bemsi.model.User;
import com.bemsi.model.UserDetails;
import com.bemsi.repository.SpecializationRepository;
import com.bemsi.repository.UserDetailsRepository;
import com.bemsi.repository.UserRepository;
import com.bemsi.security.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserDetailsRepository userDetailsRepository;
    private final SpecializationRepository specializationRepository;
    private final JdbcTemplate jdbcTemplate;

    private final LoginPasswordGenerator loginPasswordGenerator;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserDetailsRepository userDetailsRepository,
                           SpecializationRepository specializationRepository, JdbcTemplate jdbcTemplate,
                           LoginPasswordGenerator loginPasswordGenerator, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.userDetailsRepository = userDetailsRepository;
        this.specializationRepository = specializationRepository;
        this.jdbcTemplate = jdbcTemplate;
        this.loginPasswordGenerator = loginPasswordGenerator;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public UserDto signUp(UserDetailsDto userDetailsDto) {
        Specialization specialization = specializationRepository.findByName(userDetailsDto.specialization());
        if(specialization == null && (userDetailsDto.role() & 2) == 2){
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Zły lekarz");
        } //TODO Gdzie to powinno być i czy tak to robić // ja bym to robiła wcześniej w kontrolerze
        if(specialization != null && (userDetailsDto.role() & 2) == 0){
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Ta osoba nie jest lekarzem");
        }

        String login = loginPasswordGenerator.generateLogin();
        String password = loginPasswordGenerator.generatePassword();

        User user = new User(login, password, true);
        userRepository.save(user);

        UserDetails userDetails = new UserDetails(Integer.parseInt(login), specialization, userDetailsDto);
        userDetailsRepository.save(userDetails);
        return UserMapper.toUserDto(user);

    }

    @Override
    public UserDetailsDto findUserDetailsByLogin(String login){
        return userDetailsRepository
                .findById(Long.parseLong(login)).
                map(UserMapper::toUserDetailsDto).
                orElse(null);
    }


    //TODO Usunąć poniższe z prdukcji!!! tylko do testowania
    @Override
    public List<UserDto> findAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map(UserMapper::toUserDto).toList();
    }

    @Override
    public List<UserDetailsDto> findAllDetails() {
        List<UserDetails> userDetails = userDetailsRepository.findAll();
        return userDetails.stream().map(UserMapper::toUserDetailsDto).toList();
    }

    @Override
    public List<UserDetailsDto> findAllDoctors() {
        List<UserDetails> userDetails = userDetailsRepository.findAllDoctors();
        return userDetails.stream().map(UserMapper::toUserDetailsDto).toList();
    }

}
