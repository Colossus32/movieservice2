package com.colossus.movieservice2.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false, unique = true)
    @Email
    private String email;

    @Column(nullable = false, unique = true)
    @Pattern(regexp = "^[a-zA-Z]*$",
            message = "Username must contain only Latin alphabet letters")
    private String username;

    private String name;

    public User(String email, String username, String name) {
        this.email = email;
        this.username = username;
        this.name = name;
    }
}
