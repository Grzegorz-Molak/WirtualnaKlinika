package com.bemsi.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table (name = "userLogins")
public class User {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "user_id")
    private long id;

    private String login;
    private String password;
    private boolean active;

    public User(String login, String password, boolean active){
        this.login = login;
        this.password = password;
        this.active = active;
    }

}
