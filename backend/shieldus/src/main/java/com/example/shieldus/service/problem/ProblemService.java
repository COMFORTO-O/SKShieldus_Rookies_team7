package com.example.shieldus.service.problem;

import com.example.shieldus.controller.dto.*;
import com.example.shieldus.entity.member.Member;
import com.example.shieldus.entity.member.MemberSubmitProblem;
import com.example.shieldus.entity.problem.Problem;
import com.example.shieldus.entity.problem.ProblemTestCase;
import com.example.shieldus.exception.CustomException;
import com.example.shieldus.exception.ErrorCode;
import com.example.shieldus.repository.member.MemberRepository;
import com.example.shieldus.repository.member.MemberSubmitProblemRepository;
import com.example.shieldus.repository.problem.ProblemRepository;
import com.example.shieldus.repository.problem.ProblemTestCaseRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProblemService {

    private final ProblemRepository problemRepository;
    private final ProblemTestCaseRepository problemTestCaseRepository;
    private final MemberRepository memberRepository;
    private final MemberSubmitProblemRepository memberSubmitProblemRepository; // 추가


    public Page<ProblemResponseDto> getFilteredProblems(
            Long memberId, String category, Integer level, String title, String status, Pageable pageable) {
        try {
            return problemRepository.findProblemsWithFilters(memberId, category, level, title, status, pageable);
        } catch (DataAccessException e) {
            log.error("Database error in getFilteredProblems", e);
            throw new CustomException(ErrorCode.DATABASE_ERROR, e);
        }
    }

    @Transactional
    public Long createProblem(ProblemRequestDto.Create dto, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Problem problem = Problem.builder()
                .category(dto.getCategory())
                .title(dto.getTitle())
                .detail(dto.getDetail())
                .level(dto.getLevel())
                .isDeleted(false)
                .member(member)
                .build();
        problemRepository.save(problem);

        List<ProblemTestCase> testCaseList = dto.getTestCase().stream()
                .map(testCaseDto -> ProblemTestCase.builder()
                        .isTestCase(true)
                        .input(testCaseDto.getInput())
                        .output(testCaseDto.getOutput())
                        .problem(problem)
                        .build())
                .collect(Collectors.toList());
        problemTestCaseRepository.saveAll(testCaseList);
        return problem.getId();
    }

    @Transactional
    public Long updateProblem(ProblemRequestDto.Update dto, Long memberId) {
        Problem problem = problemRepository.findById(dto.getProblemId())
                .orElseThrow(() -> new CustomException(ErrorCode.PROBLEM_NOT_FOUND));
        problem.update(dto);

        List<ProblemTestCase> testCaseList = new ArrayList<>();
        for (ProblemTestCaseRequestDto.Update testCaseDto : dto.getTestCase()) {
            if (testCaseDto.getTestCaseId() != null) {
                ProblemTestCase testCase = problemTestCaseRepository.findById(testCaseDto.getTestCaseId())
                        .orElseThrow(() -> new CustomException(ErrorCode.PROBLEM_TEST_CASE_NOT_FOUND));
                testCase.update(testCaseDto);
            } else {
                ProblemTestCase testCase = ProblemTestCase.builder()
                        .isTestCase(true)
                        .input(testCaseDto.getInput())
                        .output(testCaseDto.getOutput())
                        .problem(problem)
                        .build();
                testCaseList.add(testCase);
            }
        }
        problemTestCaseRepository.saveAll(testCaseList);
        return problem.getId();
    }

    public ProblemResponseDto.Detail getProblemDetail(Long problemId) {
        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(() -> new CustomException(ErrorCode.PROBLEM_NOT_FOUND));
        return ProblemResponseDto.Detail.fromProblem(problem);
    }

    public List<ProblemTestCaseResponseDto.Detail> getProblemTestCaseListByProblemId(Boolean isTestCase, Long problemId) {
        List<ProblemTestCase> testCases = (isTestCase != null && isTestCase) ?
                problemTestCaseRepository.findByProblem_IdAndIsTestCaseIsTrue(problemId) :
                problemTestCaseRepository.findByProblem_Id(problemId);
        return testCases.stream()
                .map(ProblemTestCaseResponseDto.Detail::fromEntity)
                .collect(Collectors.toList());
    }

    public void deleteProblem(Long problemId, Long memberId) {
        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(() -> new CustomException(ErrorCode.PROBLEM_NOT_FOUND));
        if (!problem.getMember().getId().equals(memberId)) {
            throw new CustomException(ErrorCode.FORBIDDEN, "문제 작성자만 삭제할 수 있습니다.");
        }
        problemRepository.delete(problem);
    }

    public Page<SolvedProblemResponseDto> getSolvedProblemList(
            Long problemId, Long memberId, Pageable pageable) {
        try {
            // 1. 문제 존재 여부 확인
            Problem problem = problemRepository.findById(problemId)
                    .orElseThrow(() -> new CustomException(ErrorCode.PROBLEM_NOT_FOUND));
            // 2. 풀이 기록 조회 (MemberSubmitProblemRepository 사용)
            Page<MemberSubmitProblem> submitProblems = memberSubmitProblemRepository
                    .findByProblemIdAndMemberId(problemId, memberId, pageable);
            // 3. 엔티티 → DTO 변환
            return submitProblems.map(SolvedProblemResponseDto::fromEntity);
        } catch (DataAccessException e) {
            log.error("데이터베이스 오류 발생 - 문제 풀이 기록 조회 실패: problemId={}, memberId={}", problemId, memberId, e);
            throw new CustomException(ErrorCode.DATABASE_ERROR, e);
        } catch (Exception e) {
            log.error("알 수 없는 오류 발생 - 문제 풀이 기록 조회 실패", e);
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR, e);
        }
    }


}

