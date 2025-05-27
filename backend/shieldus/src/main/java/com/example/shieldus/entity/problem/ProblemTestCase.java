package com.example.shieldus.entity.problem;


import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity(name = "problem_test_case")
public class ProblemTestCase extends BaseEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id", nullable = false)

    private Problem problem;

    @Lob
    @Column(nullable = false)
    private String input;

    @Lob
    @Column(nullable = false)
    private String output;
}
