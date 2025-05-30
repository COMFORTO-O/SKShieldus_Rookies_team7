package com.example.shieldus.service.problem;

import com.example.shieldus.controller.dto.ProblemResponseDto;
import com.example.shieldus.entity.problem.Problem;
import com.example.shieldus.exception.CustomException;
import com.example.shieldus.exception.ErrorCode;
import com.example.shieldus.repository.problem.ProblemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
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
        try {
            return problemRepository.findProblemsWithFilters(
                    memberId, category, level, title, status, pageable
            );
        } catch (DataAccessException e) {
            log.error("Database error in getFilteredProblems", e);
            throw new CustomException(ErrorCode.DATABASE_ERROR, e);
        } catch (Exception e) {
            log.error("Unexpected error in getFilteredProblems", e);
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR, e);
        }
    }

    public void deleteProblem(Long problemId, Long memberId) {
        try {
            Problem problem = problemRepository.findById(problemId)
                    .orElseThrow(() -> new CustomException(ErrorCode.PROBLEM_NOT_FOUND));

            // 문제 작성자만 삭제할 수 있도록 검증
            if (!problem.getMember().getId().equals(memberId)) {
                throw new CustomException(ErrorCode.FORBIDDEN, "문제 작성자만 삭제할 수 있습니다.");
            }

            problemRepository.delete(problem);
        } catch (DataAccessException e) {
            log.error("Database error in deleteProblem", e);
            throw new CustomException(ErrorCode.DATABASE_ERROR, e);
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error in deleteProblem", e);
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR, e);
        }
    }
}