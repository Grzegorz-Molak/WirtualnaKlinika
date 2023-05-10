package com.bemsi.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_details_id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "specialization_id", nullable = true)
    private Specialization specializations;

    @OneToOne
    @JoinColumn(name="user_id",nullable = false)
    private User user_id;

    private String email;
    private String firstName;
    private String lastName;

    private LocalDate birthDate;
    private int role; //binary

}
