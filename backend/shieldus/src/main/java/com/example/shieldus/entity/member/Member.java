package com.example.shieldus.entity.member;

import com.example.shieldus.entity.member.enumration.MemberRoleEnum;
import jakarta.persistence.*;
        import lombok.*;

import java.time.LocalDateTime;

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

    @Column(nullable = true)
    private Boolean isDeleted = false;

    private LocalDateTime deletedOn;


    public void delete(){
        this.isDeleted = true;
        this.deletedOn = LocalDateTime.now();
    }

}
