package com.example.shieldus.repository.problem;

import com.example.shieldus.entity.problem.Problem;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProblemRepository extends JpaRepository<Problem, Long>, ProblemRepositoryCustom {
    Optional<Problem> findByIdAndIsDeletedIsFalse(@NotNull(message = "문제 ID가 필요합니다.") Long problemId);
}
