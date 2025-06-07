package com.example.shieldus.repository.problem;

import com.example.shieldus.entity.problem.ProblemCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProblemCodeRepository extends JpaRepository<ProblemCode, Long> {
    Optional<ProblemCode> findByCode(String code);
}
