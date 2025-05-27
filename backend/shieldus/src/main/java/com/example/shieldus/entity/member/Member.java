package com.example.shieldus.entity.member;

import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "member")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // auto increment, unique, primary key

    @Column(nullable = false, unique = true)
    private String email;

    private String password;
    private String name;
    private String phone;
    private String rank;

    @ManyToOne
    @JoinColumn(name = "member_role_id")
    private MemberRole memberRole;
}
