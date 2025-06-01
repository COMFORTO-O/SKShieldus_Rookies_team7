package com.example.shieldus.service.problem;

import com.example.shieldus.controller.dto.ProblemRequestDto;
import com.example.shieldus.controller.dto.ProblemResponseDto;
import com.example.shieldus.controller.dto.ProblemTestCaseRequestDto;
import com.example.shieldus.controller.dto.ProblemTestCaseResponseDto;
import com.example.shieldus.entity.member.Member;
import com.example.shieldus.entity.problem.Problem;
import com.example.shieldus.entity.problem.ProblemTestCase;
import com.example.shieldus.exception.CustomException;
import com.example.shieldus.exception.ErrorCode;
import com.example.shieldus.repository.member.MemberRepository;
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
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProblemService {

    private final ProblemRepository problemRepository;
    private final ProblemTestCaseRepository problemTestCaseRepository;
    private final MemberRepository memberRepository;

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

    /*
    * 문제 생성
    * */
    @Transactional
    public Long createProblem(ProblemRequestDto.Create dto, Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        try {
            // 문제 생성
            Problem problem = Problem.builder()
                    .category(dto.getCategory())
                    .title(dto.getTitle())
                    .detail(dto.getDetail())
                    .level(dto.getLevel())
                    .isDeleted(false)
                    .member(member)
                    .build();

            problemRepository.save(problem);
            // 문제 테스트 케이스 생성
            List<ProblemTestCase> testCaseList = new ArrayList<>();

            for (ProblemTestCaseRequestDto.Create testCaseDto : dto.getTestCase()) {
                ProblemTestCase testCase = ProblemTestCase.builder()
                        .isTestCase(true)
                        .input(testCaseDto.getInput())
                        .output(testCaseDto.getOutput())
                        .problem(problem)
                        .build();
                testCaseList.add(testCase);
            }
            problemTestCaseRepository.saveAll(testCaseList);

            return problem.getId();
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR, e);
        }
    }
    /*
    * 문제 업데이트
    * */
    @Transactional
    public Long updateProblem(ProblemRequestDto.Update dto, Long memberId) {
        Problem problem = problemRepository.findById(dto.getProblemId()).orElseThrow(() -> new CustomException(ErrorCode.PROBLEM_NOT_FOUND));

        try {
            problem.update(dto);
            // 문제 테스트 케이스 생성
            List<ProblemTestCase> testCaseList = new ArrayList<>();
            for (ProblemTestCaseRequestDto.Update testCaseDto : dto.getTestCase()) {
                // 아이디가 있는 경우
                if(testCaseDto.getTestCaseId() != null && testCaseDto.getTestCaseId() > 0L) {
                    ProblemTestCase testCase = problemTestCaseRepository.findById(testCaseDto.getTestCaseId())
                            // todo: testCase 오류로 변경
                            .orElseThrow(()-> new CustomException(ErrorCode.PROBLEM_TEST_CASE_NOT_FOUND));
                    if(!testCase.getProblem().equals(problem)) {
                        throw new CustomException(ErrorCode.PROBLEM_TEST_CASE_NOT_FOUND);
                    }
                    testCase.update(testCaseDto);
                }
                // 아이디가 없는 경우. 새로 생성
                else{
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
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR, e);
        }
    }
    /*
    * 문제 상세정보 가져오기
    * */

    public ProblemTestCaseResponseDto.Detail getProblemTestCaseDetail(Long testCaseId) {
        ProblemTestCase testCase = problemTestCaseRepository.findById(testCaseId)
                .orElseThrow(() -> new CustomException(ErrorCode.PROBLEM_TEST_CASE_NOT_FOUND));

        return ProblemTestCaseResponseDto.Detail.fromEntity(testCase);
    }
    public List<ProblemTestCaseResponseDto.Detail> getProblemTestCaseListByProblemId(Boolean isTestCase,Long problemId) {
        if(isTestCase == null){
            isTestCase = false;
        }

        List<ProblemTestCase> testCase = isTestCase ?
                problemTestCaseRepository.findByProblem_IdAndIsTestCaseIsTrue(problemId) :
                problemTestCaseRepository.findByProblem_Id(problemId);
        return  testCase.stream().map(ProblemTestCaseResponseDto.Detail::fromEntity).toList();
    }

    public ProblemResponseDto.Detail getProblemDetail(Long problemId) {
        Problem problem = problemRepository.findById(problemId).orElseThrow(() -> new CustomException(ErrorCode.PROBLEM_NOT_FOUND));
        return ProblemResponseDto.Detail.fromProblem(problem);
    }

}

