package com.example.shieldus.entity.problem;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "problem_code")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProblemCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code; // Java, Python, C

    private String description; //Java , Python, C

    public ProblemCode(String code, String description) {
        this.code = code;
        this.description = description;

    }

}
