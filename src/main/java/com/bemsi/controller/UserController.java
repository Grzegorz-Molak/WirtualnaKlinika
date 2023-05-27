package com.bemsi.controller;

import com.bemsi.DTOs.mapper.UserMapper;
import com.bemsi.DTOs.model.UserDetailsDto;
import com.bemsi.DTOs.model.UserDto;
import com.bemsi.model.UserDetails;
import com.bemsi.repository.UserDetailsRepository;
import com.bemsi.security.AccessService;
import com.bemsi.security.JwtService;
import com.bemsi.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;


@CrossOrigin (origins={"https://localhost:3000"}, allowCredentials = "true")
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    private final UserDetailsRepository userDetailsRepository;
    private final JwtService jwtService;

    private final AccessService accessService;

    @Autowired
    public UserController(UserService userService,
                          JwtService jwtService,
                          AccessService accessService,
                          UserDetailsRepository userDetailsRepository) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.accessService = accessService;
        this.userDetailsRepository = userDetailsRepository;
    }

    @PostMapping("/signup")
    private UserDto signUp(@RequestBody UserDetailsDto userDetailsDto) {
        return userService.signUp(userDetailsDto);
    }

    @PostMapping("/login")
    private ResponseEntity<String> logIn(@RequestBody UserDto userDto) {
        return userService.logIn(userDto);
    }

    //TODO Sprawdzanie tokenu
    //Tylko zalogowany użytkownik może dostać swoje dane
    @GetMapping("/{profile}")
    private UserDetailsDto userDetailsByLogin(@PathVariable String profile,  @CookieValue(name="token") String token) {
        UserDetails user = jwtService.authorizationCookie(token);
        accessService.validateAccess(AccessService.Resource.PROFILE,
                user,
                Long.parseLong(profile));
        Optional<UserDetails> searched_profile = userDetailsRepository.findById(Long.parseLong(profile));
        if(searched_profile.isEmpty()) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized!\n");
        return UserMapper.toUserDetailsDto(searched_profile.get());
    }

    @PostMapping("logout")
    private String jwtInvalidateToken(@CookieValue(name="token") String token){
        String jwt = token;
        if (jwtService.validateJws(jwt).isPresent()) {
            jwtService.invalidateToken(jwt);
            return "Unieważniono token";
        } else {
            return "Nieprawidłowy token";
        }
    }


    //TODO Usunąć poniższe z prdukcji!!! tylko do testowania
    @GetMapping("")
    private List<UserDto> allUsers() {
        return userService.findAllUsers();
    }

    @GetMapping("/details")
    private List<UserDetailsDto> allUserDetails() {
        return userService.findAllDetails();
    }

    @GetMapping("/doctors")
    private List<UserDetailsDto> allDoctorsDetails() {
        return userService.findAllDoctors();
    }

    @GetMapping("accesses")
    private String showAccesses(){
        return Arrays.toString(AccessService.Resource.values());
    }


}

