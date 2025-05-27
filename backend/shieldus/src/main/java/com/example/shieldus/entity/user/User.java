// User.java
package com.example.shieldus.entity.user;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    private String password;
    private String name;
    private String email;
    private String phone;
    private String rank;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private UserRole role;
}
