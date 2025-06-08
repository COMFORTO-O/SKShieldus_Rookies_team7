package com.example.shieldus.repository.problem;

import com.example.shieldus.controller.dto.ProblemResponseDto;
import com.example.shieldus.controller.dto.SubmissionDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProblemRepositoryCustom {
    Page<ProblemResponseDto> findProblemsWithFilters(
            Long memberId,
            String category,
            Integer level,
            String title,
            Boolean status,
            Pageable pageable
    );

    Page<SubmissionDto> getSubmissions(Long memberId, Pageable pageable);
}