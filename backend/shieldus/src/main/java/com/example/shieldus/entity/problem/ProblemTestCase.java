package com.example.shieldus.entity.problem;


import com.example.shieldus.controller.dto.ProblemTestCaseRequestDto;
import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "problem_test_case")
public class ProblemTestCase extends BaseEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id", nullable = false)
    private Problem problem;

    private Boolean isTestCase;
    @Lob
    @Column(nullable = false)
    private String input;

    @Lob
    @Column(nullable = false)
    private String output;

    public void update(ProblemTestCaseRequestDto.Update testCaseDto) {
        this.input = testCaseDto.getInput();
        this.output = testCaseDto.getOutput();
    }
}
