package com.example.shieldus.repository.problem;

import com.example.shieldus.entity.problem.Problem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProblemRepository
        extends JpaRepository<Problem, Long>, ProblemRepositoryCustom {
    // 기본 CRUD + ProblemRepositoryCustom에서 정의한 동적 검색 메서드 제공
}