package com.example.shieldus.entity.member;

import com.example.shieldus.entity.problem.Problem;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "member_submit_problem")
public class MemberSubmitProblem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne
    @JoinColumn(name = "problem_id", nullable = false)
    private Problem problem;

    private Boolean pass;  // true or false

    private LocalDateTime completeDate;

    private Long userTempCodeId;
}