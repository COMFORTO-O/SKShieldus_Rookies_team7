package com.example.shieldus.entity.member;

import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "member_role")
public class MemberRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long adminRole;

    private String memberRole;
}
