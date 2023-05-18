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

import com.bemsi.security.JwtService;
import com.bemsi.security.LoginPasswordGenerator;
import com.bemsi.security.PasswordEncoder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserDetailsRepository userDetailsRepository;
    private final SpecializationRepository specializationRepository;

    private final LoginPasswordGenerator loginPasswordGenerator;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserDetailsRepository userDetailsRepository,
                           SpecializationRepository specializationRepository, LoginPasswordGenerator loginPasswordGenerator,
                           PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.userDetailsRepository = userDetailsRepository;
        this.specializationRepository = specializationRepository;
        this.loginPasswordGenerator = loginPasswordGenerator;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }


    @Override
    public String logIn(UserDto userDto) {

        boolean isSuccess = passwordEncoder.validatePassword(
                userDto.password(), userRepository.findByLogin(userDto.login()).getPassword());

        if (isSuccess) {
            return jwtService.generateJws(userDto.login());
        }

        return "Podano niepoprawne dane";
    }

    @Override
    public UserDto signUp(UserDetailsDto userDetailsDto) {

        Specialization specialization = specializationRepository.findByName(userDetailsDto.specialization());
        if (specialization == null && (userDetailsDto.role() & 2) == 2) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Nie ma takiej specjalizacji");
        }

        if (specialization != null && (userDetailsDto.role() & 2) == 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Ta osoba nie jest lekarzem, więc nie może mieć specjalizacji");
        }

        String login = loginPasswordGenerator.generateLogin();
        String password = loginPasswordGenerator.generatePassword();
        String hash = passwordEncoder.createHash(password);
        User user = new User(login, hash, true);
        userRepository.save(user);
        UserDetails userDetails = new UserDetails(Integer.parseInt(login), specialization, userDetailsDto);
        userDetailsRepository.save(userDetails);

        User user1 = new User(login, password, true);

        return UserMapper.toUserDto(user1);

    }

    @Override
    public UserDetailsDto findUserDetailsByLogin(String login) {
        System.out.println(login);
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
