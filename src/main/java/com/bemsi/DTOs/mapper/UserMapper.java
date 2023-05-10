package com.bemsi.DTOs.mapper;

import com.bemsi.DTOs.model.UserDetailsDto;
import com.bemsi.DTOs.model.UserDto;
import com.bemsi.model.User;
import com.bemsi.model.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public static UserDto toUserDto (User user){
        return new UserDto(user.getLogin(),user.getPassword());
    }

    public static UserDetailsDto toUserDetailsDto (UserDetails userDetails){
        return new UserDetailsDto(userDetails.getFirstName(),userDetails.getLastName(),
                userDetails.getEmail(),userDetails.getSpecialization().getName(),userDetails.getBirthDate(),userDetails.getRole());
    }
}
