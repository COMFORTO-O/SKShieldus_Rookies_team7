package com.example.shieldus.service.problem;

import com.example.shieldus.controller.dto.ProblemResponseDto;
import com.example.shieldus.repository.problem.ProblemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProblemService {

    private final ProblemRepository problemRepository;

    public Page<ProblemResponseDto> getFilteredProblems(
            Long memberId,
            String category,
            Integer level,
            String title,
            String status,
            Pageable pageable) {

        return problemRepository.findProblemsWithFilters(
                memberId, category, level, title, status, pageable
        );
    }
}