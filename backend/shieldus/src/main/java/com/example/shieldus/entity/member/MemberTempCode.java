package com.example.shieldus.entity.member;


import com.example.shieldus.entity.enumration.MemberTempCodeStatusEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "member_temp_code")
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


}
