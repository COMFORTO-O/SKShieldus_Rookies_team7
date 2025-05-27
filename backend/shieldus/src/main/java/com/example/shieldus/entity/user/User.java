package com.example.shieldus.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false, unique = true)
    private String username;

    private String password;
    private String name;
    private String email;
    private String phone;
    private String rank;

    @ManyToOne
    @JoinColumn(name = "user_role_fk")
    private UserRole userRole;
}
