package com.example.shieldus.controller;

import com.example.shieldus.controller.dto.ProblemResponseDto;
import com.example.shieldus.controller.dto.ResponseDto;
import com.example.shieldus.service.problem.ProblemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/problem")
@RequiredArgsConstructor
public class ProblemController {

    private final ProblemService problemService;

    @GetMapping
    public ResponseDto<Page<ProblemResponseDto>> getProblems(
            @RequestParam Long memberId,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer level,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String status,
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {

        Page<ProblemResponseDto> page = problemService.getFilteredProblems(
                memberId, category, level, title, status, pageable
        );

        return ResponseDto.success(page);
    }
}