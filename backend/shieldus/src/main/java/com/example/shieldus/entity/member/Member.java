package com.example.shieldus.entity.member;

import jakarta.persistence.*;
        import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "member")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // auto increment, unique, primary key

    private String email;
    private String password;
    private String name;
    private String phone;
    private Integer memberRank;

}
