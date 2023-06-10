package com.bemsi.service;

import com.bemsi.AppointmentGenerator;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserDetailsRepository userDetailsRepository;
    private final SpecializationRepository specializationRepository;

    private final LoginPasswordGenerator loginPasswordGenerator;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    private final AppointmentGenerator appointmentGenerator;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserDetailsRepository userDetailsRepository,
                           SpecializationRepository specializationRepository, LoginPasswordGenerator loginPasswordGenerator,
                           PasswordEncoder passwordEncoder, JwtService jwtService, AppointmentGenerator appointmentGenerator) {
        this.userRepository = userRepository;
        this.userDetailsRepository = userDetailsRepository;
        this.specializationRepository = specializationRepository;
        this.loginPasswordGenerator = loginPasswordGenerator;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.appointmentGenerator = appointmentGenerator;
    }


    @Override
    public ResponseEntity<String> logIn(UserDto userDto) {
        if(userRepository.findByLogin(userDto.login()) == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Podano niepoprawne dane");

        boolean isSuccess = passwordEncoder.validatePassword(
                userDto.password(), userRepository.findByLogin(userDto.login()).getPassword());

        if (isSuccess) {
            LocalDateTime expirationDateTime = LocalDateTime.now().plusMinutes(5);
            String token =  jwtService.generateJws(userDto.login());
            long expirationTimestamp = expirationDateTime.toEpochSecond(ZoneOffset.UTC);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.SET_COOKIE, "token=" + token + "; Path=/; Expires=" + expirationTimestamp + ";HttpOnly;Secure;");

            return ResponseEntity.ok().headers(headers).body("Zalogowano pomyślnie");
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Podano niepoprawne dane");
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

        if(userDetailsRepository.existsByEmail(userDetailsDto.email())){
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Email zajęty");
        }

        String login = loginPasswordGenerator.generateLogin();
        String password = loginPasswordGenerator.generatePassword();
        String hash = passwordEncoder.createHash(password);
        User user = new User(login, hash, true);
        userRepository.save(user);
        UserDetails userDetails = new UserDetails(Integer.parseInt(login), specialization, userDetailsDto);
        userDetailsRepository.save(userDetails);

        if((userDetailsDto.role() & 2) == 2){
            List<UserDetails> doctors = new ArrayList<>();
            doctors.add(userDetails);
            appointmentGenerator.generateAppointments(doctors);
        }

        User user1 = new User(login, password, true);

        return UserMapper.toUserDto(user1);

    }

    @Override
    public String changePassword(String profile, String oldPassword, String newPassword){
        User user = userRepository.findByLogin(profile);

        if(newPassword.length()<12){
            return "Za krótkie hasło";
        }
        if(!passwordEncoder.validatePassword(oldPassword, user.getPassword())){
            return "Stare hasło jest niepoprawne";
        }

        user.setPassword(passwordEncoder.createHash(newPassword));
        userRepository.save(user);

        return "Udało sie zmienić hasło";

    }

    @Override
    public UserDetailsDto findUserDetailsByLogin(String login) {
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
