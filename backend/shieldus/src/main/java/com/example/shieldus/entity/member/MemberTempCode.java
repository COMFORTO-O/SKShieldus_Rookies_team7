package com.example.shieldus.entity.member;


import com.example.shieldus.entity.member.enumration.MemberTempCodeStatusEnum;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "member_temp_code")
public class MemberTempCode {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_submit_problem_id")
    private MemberSubmitProblem memberSubmitProblem;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private MemberTempCodeStatusEnum status;

    @Column(name= "language")
    private String langauge;

    @Column(name= "code")
    private String code;

    @Column(updatable = false)
    private LocalDateTime submitDate;
}
