package com.example.shieldus.entity.member;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class MemberRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long adminRole;

    private String memberRole;
}