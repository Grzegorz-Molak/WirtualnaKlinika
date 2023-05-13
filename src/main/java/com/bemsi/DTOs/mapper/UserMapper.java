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
        if(userDetails == null) return null;
        return new UserDetailsDto(
                userDetails.getFirstName(),
                userDetails.getLastName(),
                userDetails.getEmail(),
                ((userDetails.getRole() & 2) == 2 ? userDetails.getSpecialization().getName() : null),
                userDetails.getBirthDate(),
                userDetails.getRole());
    }
}
