package com.bemsi.DTOs.model;

import java.time.LocalDate;


public record UserDetailsDto(String firstName,
        String lastName,
        String email,
        String specialization,
        LocalDate birthDate,
        int role){

}
