package com.bemsi.security;


import org.springframework.beans.factory.annotation.Autowired;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class main {

    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String password = "password";

        PasswordEncoder ps = new PasswordEncoder();

        String hash = ps.createHash(password);
        System.out.println(hash);

        Boolean result = ps.validatePassword(password,hash);
        System.out.println(result);
    }
}
