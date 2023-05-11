package com.bemsi.model;

import com.bemsi.DTOs.model.UserDetailsDto;
import com.bemsi.repository.SpecializationRepository;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table (name = "user_details")
public class UserDetails {
    @Id
    @Column(name = "user_details_id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "specialization_id", nullable = true)
    private Specialization specialization;

    private String email;
    private String firstName;
    private String lastName;

    private LocalDate birthDate;
    private int role; //binary

    public UserDetails(Long id, Specialization specialization, UserDetailsDto userDetailsDto){
        this.id = id;
        this.specialization = specialization;
        this.email = userDetailsDto.getEmail();
        this.firstName = userDetailsDto.getFirstName();
        this.lastName = userDetailsDto.getLastName();
        this.birthDate =  userDetailsDto.getBirthDate();
        this.role = userDetailsDto.getRole();
    }
}
