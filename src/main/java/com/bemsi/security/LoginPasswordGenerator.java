package com.bemsi.security;

import com.bemsi.repository.UserDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class LoginPasswordGenerator {
    private final Random random = new Random();
    private final UserDetailsRepository userDetailsRepository;

    @Autowired
    public LoginPasswordGenerator(UserDetailsRepository userDetailsRepository){
        this.userDetailsRepository = userDetailsRepository;
    }

    public String generateLogin(){
        long random_id;
        do{
            random_id = random.nextInt(9999999);
        }while(userDetailsRepository.existsById(random_id));
        return String.format("%07d", random_id);
    }

    public String generatePassword(){
        int STRING_LENGTH = 6;
        String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        StringBuilder password = new StringBuilder(STRING_LENGTH);
        for (int i = 0; i < STRING_LENGTH; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            char randomChar = CHARACTERS.charAt(randomIndex);
            password.append(randomChar);
        }
        return password.toString();
    }
}
