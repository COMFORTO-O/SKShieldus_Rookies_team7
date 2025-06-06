package com.example.shieldus.entity.member;

import com.example.shieldus.controller.dto.MemberRequestDto;
import com.example.shieldus.entity.member.enumration.MemberRoleEnum;
import com.example.shieldus.entity.problem.BaseEntity;
import jakarta.persistence.*;
        import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "member")
public class Member extends BaseEntity {

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

    @Column(nullable = true)
    @Builder.Default
    private Boolean isDeleted = false;

    private LocalDateTime deletedAt;


    public void delete(){
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
    }



}
