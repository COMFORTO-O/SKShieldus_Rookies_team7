package com.example.shieldus.service.problem;

import com.example.shieldus.controller.dto.ProblemResponseDto;
import com.example.shieldus.entity.problem.Problem;
import com.example.shieldus.exception.CustomException;
import com.example.shieldus.exception.ErrorCode;
import com.example.shieldus.repository.problem.ProblemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProblemService {

    private final ProblemRepository problemRepository;

    // 모든 문제 조회 (예외 처리 추가)
    public List<ProblemResponseDto> getAllProblems() {
        try {
            List<Problem> problems = problemRepository.findAll();

            // 문제가 없을 경우 빈 리스트 반환 (예외 X)
            // 필요 시 아래 주석 해제하여 PROBLEM_NOT_FOUND 예외 발생 가능
            /*
            if (problems.isEmpty()) {
                throw new CustomException(ErrorCode.PROBLEM_NOT_FOUND);
            }
            */

            return problems.stream()
                    .map(problem -> ProblemResponseDto.builder()
                            .id(problem.getId())
                            .title(problem.getTitle())
                            .detail(problem.getDetail())
                            .category(problem.getCategory())
                            .level(problem.getLevel())
                            .memberName(problem.getMember().getName())
                            .build())
                    .collect(Collectors.toList());

        } catch (DataAccessException e) {
            // DB 조회 실패 시
            throw new CustomException(ErrorCode.DATABASE_ERROR, e);
        }
    }
}
