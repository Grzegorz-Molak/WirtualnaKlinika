package com.bemsi.controller;

import com.bemsi.DTOs.model.UserDetailsDto;
import com.bemsi.DTOs.model.UserDto;
import com.bemsi.model.User;
import com.bemsi.model.UserDetails;
import com.bemsi.security.JwtService;
import com.bemsi.service.UserService;
import io.jsonwebtoken.Jwt;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;
    @Autowired
    public UserController(UserService userService, JwtService jwtService){
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping("/signup")
    private UserDto signUp(@RequestBody UserDetailsDto userDetailsDto){
        return userService.signUp(userDetailsDto);
    }

    @GetMapping("/login")
    private String logIn(@RequestBody UserDto userDto){
        return userService.logIn(userDto);
    }

    //TODO Usunąć poniższe z prdukcji!!! tylko do testowania
    @GetMapping("")
    private List<UserDto> allUsers(){
        return userService.findAllUsers();
    }

    @GetMapping("/{login}")
    private UserDetailsDto userDetailsByLogin(@PathVariable String login){return userService.findUserDetailsByLogin(login);}

    @GetMapping("/details")
    private List<UserDetailsDto> allUserDetails(){
        return userService.findAllDetails();
    }

    @GetMapping("/doctors")
    private List<UserDetailsDto> allDoctorsDetails(){
        return userService.findAllDoctors();
    }

    @GetMapping("/jwt/{login}")
    private String jwtGen(@PathVariable String login) throws Exception {
        UserDetailsDto userDetailsDto = userService.findUserDetailsByLogin(login); //znaleziony user
        if(userDetailsDto == null)
            return "Nieprawidłowy user";
        String jwt = jwtService.generateJws(login);
        return jwt;
    }

    @GetMapping("/jwt/test/{token}")
    private String jwtTest(@PathVariable String token){
        if(jwtService.validateJws(token)){
            return "Witaj podróźniku";
        }
        else{
            return "Odejdź póki masz szanse";
        }
    }

    @GetMapping("jwt/testheader")
    private String jwtTestHeader(@NotNull HttpServletRequest request){
        final String header = request.getHeader("Authorization");
        String jwt;
        if(header != null && header.startsWith("Bearer ")){
            jwt = header.substring(7);
            if(jwtService.validateJws(jwt)){
                return "Witaj " + jwtService.extractUsername(jwt);
            }
            else{
                return "Odejdź póki masz szanse";
            }
        }
        else{
            return "Hamuj się!";
        }
    }


}

