package com.example.shieldus.controller;

import com.example.shieldus.controller.dto.ProblemResponseDto;
import com.example.shieldus.controller.dto.ResponseDto;
import com.example.shieldus.service.problem.ProblemService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/problems")  // 공통 URL prefix 설정
@RequiredArgsConstructor
public class ProblemController {

    private final ProblemService problemService;

    @GetMapping
    public ResponseDto<List<ProblemResponseDto>> getAllProblems() {
        // 전체 문제 목록을 서비스 계층에서 조회
        List<ProblemResponseDto> problems = problemService.getAllProblems();

        // 조회 결과를 통일된 응답 형식으로 감싸서 반환
        return ResponseDto.success(problems);
    }

//    private final ProblemService problemService;
//
//    @GetMapping
//    public List<ProblemResponseDto> getAllProblems() {
//        return problemService.getAllProblems();
//    }
}
