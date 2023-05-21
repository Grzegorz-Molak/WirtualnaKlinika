package com.bemsi.security;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@Entity
@Table(name = "invalidated_tokens")
public class InvalidatedToken{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token")
    private String token;

    @Column(name = "expiration_time")
    private Date expirationTime;

    public InvalidatedToken(String token, Date expirationTime){
        this.token = token;
        this.expirationTime = expirationTime;
    }
}
