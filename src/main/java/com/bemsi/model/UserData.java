package com.bemsi.model;

import jakarta.persistence.*;

import java.time.LocalDate;
@Entity
@Table(name = "usersData")
public class UserData {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long user_id;
    private String email;
    private String firstName;
    private String lastName;
    @OneToMany
    @PrimaryKeyJoinColumn(name = "specializations")
    private Specialization specializationId;
    private LocalDate birthDate;
    private int rola;
}
