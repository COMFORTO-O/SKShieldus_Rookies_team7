package com.example.shieldus.entity.problem;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "problem_code")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProblemCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code; // 예: "ERROR", "WARNING", "CRITICAL"

    private String description;

    // 필요시 추가 필드
}
