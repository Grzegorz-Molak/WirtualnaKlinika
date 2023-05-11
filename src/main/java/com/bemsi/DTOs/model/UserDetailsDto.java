package com.bemsi.DTOs.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


public record UserDetailsDto(String firstName,
        String lastName,
        String email,
        String specialization,
        LocalDate birthDate,
        int role){

}
