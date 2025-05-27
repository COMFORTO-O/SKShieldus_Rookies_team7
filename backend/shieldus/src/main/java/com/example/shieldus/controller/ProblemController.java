package com.example.shieldus.controller;

import com.example.shieldus.controller.dto.ProblemResponseDto;
import com.example.shieldus.service.problem.ProblemService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/problems")
@RequiredArgsConstructor
public class ProblemController {

    private final ProblemService problemService;

    @GetMapping
    public List<ProblemResponseDto> getAllProblems() {
        return problemService.getAllProblems();
    }
}
