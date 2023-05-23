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
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;



@CrossOrigin ("http://localhost:3000")
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
    private String logIn(@RequestBody UserDto userDto) {
        return userService.logIn(userDto);
    }

    //TODO Sprawdzanie tokenu
    //Tylko zalogowany użytkownik może dostać swoje dane
    @GetMapping("/{profile}")
    private UserDetailsDto userDetailsByLogin(@PathVariable String profile, @NotNull HttpServletRequest request) {
        UserDetails user = jwtService.authorization(request);
        accessService.validateAccess(AccessService.Resource.PROFILE,
                user,
                Long.parseLong(profile));
        return UserMapper.toUserDetailsDto(user);
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

    @GetMapping("/jwt/{login}")
    private String jwtGen(@PathVariable String login) throws Exception {
        UserDetailsDto userDetailsDto = userService.findUserDetailsByLogin(login); //znaleziony user
        if (userDetailsDto == null)
            return "Nieprawidłowy user";
        String jwt = jwtService.generateJws(login);
        return jwt;
    }

    @GetMapping("/jwt/test/{token}")
    private String jwtTest(@PathVariable String token) {
        if (jwtService.validateJws(token).isPresent()) {
            return "Witaj podróźniku";
        } else {
            return "Odejdź póki masz szanse";
        }
    }


    @PostMapping("jwt/invalidate")
    private String jwtInvalidateToken(@NotNull HttpServletRequest request){
        final String header = request.getHeader("Authorization");
        String jwt;
        if (header != null && header.startsWith("Bearer ")) {
            jwt = header.substring(7);
            if (jwtService.validateJws(jwt).isPresent()) {
                jwtService.invalidateToken(jwt);
                return "Unieważniono token";
            } else {
                return "Nieprawidłowy token";
            }
        } else {
            return "Hamuj się!";
        }
    }

    @GetMapping("accesses")
    private String showAccesses(){
        return Arrays.toString(AccessService.Resource.values());
    }


}

