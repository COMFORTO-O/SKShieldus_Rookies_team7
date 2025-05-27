package com.example.shieldus.entity.problem;


import com.example.shieldus.entity.member.Member;
import com.example.shieldus.entity.problem.enumration.ProblemCategoryEnum;
import jakarta.persistence.*;
import lombok.*;



@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity(name = "problem")
public class Problem extends BaseEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member memberId;

    private String title;

    private String detail;

    @Enumerated(EnumType.STRING)
    private ProblemCategoryEnum category;

    private Integer level;

}
