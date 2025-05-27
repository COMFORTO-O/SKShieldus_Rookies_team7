package com.example.shieldus.entity.member;

import com.example.shieldus.entity.enumration.MemberRoleEnum;
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


    @Enumerated(EnumType.STRING)
    private MemberRoleEnum role;

    private String email;
    private String password;
    private String name;
    private String phone;
    private Integer memberRank;

}
