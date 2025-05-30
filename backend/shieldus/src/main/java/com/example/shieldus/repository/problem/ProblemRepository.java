package com.example.shieldus.repository.problem;

import com.example.shieldus.entity.problem.Problem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProblemRepository extends JpaRepository<Problem, Long>, ProblemRepositoryCustom {
}
