package com.example.shieldus.service.problem;

import com.example.shieldus.controller.dto.ProblemResponseDto;
import com.example.shieldus.entity.problem.Problem;
import com.example.shieldus.repository.problem.ProblemRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProblemService {

    private final ProblemRepository problemRepository;

    public List<ProblemResponseDto> getAllProblems() {
        List<Problem> problems = problemRepository.findAll();

        return problems.stream().map(problem ->
                ProblemResponseDto.builder()
                        .id(problem.getId())
                        .title(problem.getTitle())
                        .detail(problem.getDetail())
                        .category(problem.getCategory())
                        .level(problem.getLevel())
                        .memberName(problem.getMember().getName())
                        .build()
        ).collect(Collectors.toList());
    }
}
