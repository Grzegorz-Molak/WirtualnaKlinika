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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Random;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserDetailsRepository userDetailsRepository;
    private final SpecializationRepository specializationRepository;
    private final JdbcTemplate jdbcTemplate;
    private final UserMapper userMapper;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserDetailsRepository userDetailsRepository,
                           SpecializationRepository specializationRepository, JdbcTemplate jdbcTemplate,
                           UserMapper userMapper){
        this.userRepository = userRepository;
        this.userDetailsRepository = userDetailsRepository;
        this.specializationRepository = specializationRepository;
        this.jdbcTemplate = jdbcTemplate;
        this.userMapper = userMapper;
    }


    @Override
    public UserDto signUp(UserDetailsDto userDetailsDto) {
        Specialization specialization = specializationRepository.findByName(userDetailsDto.getSpecialization());
        if(specialization == null && (userDetailsDto.getRole() & 2) == 2){
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Zły lekarz");
        } //TODO Gdzie to powinno być i czy tak to robić
        if(specialization != null && (userDetailsDto.getRole() % 2) == 0){
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Ta osoba nie jest lekarzem");
        }

        String login = userDetailsDto.getFirstName().charAt(0) +
                userDetailsDto.getLastName().substring(0, 4); //Co jeżeli nazwisko jest krótsze od 4 liter

        int STRING_LENGTH = 6;
        String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();

        StringBuilder password = new StringBuilder(STRING_LENGTH);
        for (int i = 0; i < STRING_LENGTH; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            char randomChar = CHARACTERS.charAt(randomIndex);
            password.append(randomChar);
        }
        User user = new User(login, password.toString(), true);
        userRepository.save(user);

        UserDetails userDetails = new UserDetails(user.getId(), specialization, userDetailsDto);
        userDetailsRepository.save(userDetails);
        return userMapper.toUserDto(user);


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

}
