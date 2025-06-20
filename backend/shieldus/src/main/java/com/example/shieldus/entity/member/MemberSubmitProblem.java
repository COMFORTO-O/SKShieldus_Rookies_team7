package com.example.shieldus.entity.member;

import com.example.shieldus.entity.problem.Problem;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
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

    private LocalDateTime completedAt;
    private LocalDateTime updatedAt;
    @OneToMany(mappedBy = "memberSubmitProblem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberTempCode> MemberTempCodes;

    private LocalDateTime createdAt; // 제출 시간 필드

}