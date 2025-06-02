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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProblemService {

    private final ProblemRepository problemRepository;
    private final ProblemTestCaseRepository testCaseRepository;
    private final MemberSubmitProblemRepository submitProblemRepository;
    private final MemberRepository memberRepository;

    /**
     * 1) 문제 목록 조회 (필터 적용, 페이징)
     *    • memberId가 null이면 “solved/unsolved” 상태 필터는 항상 false 처리
     */
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

    /**
     * 2) 문제 상세 조회 (테스트케이스 포함 + solved 여부 포함)
     */
    @Transactional(readOnly = true)
    public ProblemDetailDto getProblemDetail(Long memberId, Long problemId) {
        // 2-1) 문제 존재 여부 확인
        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(() -> new CustomException(
                        ErrorCode.PROBLEM_NOT_FOUND,
                        "Problem (id=" + problemId + ") not found"
                ));

        // 2-2) 테스트케이스 목록 조회
        List<ProblemTestCase> testCases = testCaseRepository.findByProblem(problem);
        List<ProblemDetailDto.TestCaseInfoDto> testCaseDtoList = new ArrayList<>();
        for (ProblemTestCase tc : testCases) {
            testCaseDtoList.add(ProblemDetailDto.TestCaseInfoDto.builder()
                    .id(tc.getId())
                    .input(tc.getInput())
                    .output(tc.getOutput())
                    .build());
        }

        // 2-3) 로그인 유저가 해당 문제를 풀었는지 확인
        boolean solved = false;
        if (memberId != null) {
            Optional<MemberSubmitProblem> mspOpt =
                    submitProblemRepository.findByMemberIdAndProblemId(memberId, problemId);
            solved = mspOpt.map(MemberSubmitProblem::getPass).orElse(false);
        }

        // 2-4) 문제 작성자 이름 조회
        String writerName = problem.getMember().getName();

        // 2-5) DTO 빌드 후 반환
        return ProblemDetailDto.builder()
                .id(problem.getId())
                .title(problem.getTitle())
                .detail(problem.getDetail())
                .category(problem.getCategory().name())
                .level(problem.getLevel())
                .memberName(writerName)
                .solved(solved)
                .createdAt(problem.getCreatedAt())
                .updatedAt(problem.getUpdatedAt())
                .testCase(testCaseDtoList)
                .build();
    }

    /**
     * 3) 문제 생성 (Problem + TestCase를 함께 저장)
     */
    @Transactional
    public CreateProblemResponseDto createProblem(Long memberId, CreateProblemRequestDto dto) {
        // 3-1) 회원 존재 여부 체크
        Member writer = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(
                        ErrorCode.USER_NOT_FOUND,
                        "Member (id=" + memberId + ") not found"
                ));

        // 3-2) Problem 엔티티 생성 및 저장
        Problem problem = Problem.builder()
                .member(writer)
                .title(dto.getTitle())
                .detail(dto.getDetail())
                .category(Enum.valueOf(
                        com.example.shieldus.entity.problem.enumration.ProblemCategoryEnum.class,
                        dto.getCategory().toUpperCase()
                ))
                .level(dto.getLevel())
                .build();
        problem = problemRepository.save(problem);

        // 3-3) TestCase 엔티티 목록 생성 및 저장
        List<ProblemTestCase> toSave = new ArrayList<>();
        for (CreateProblemRequestDto.TestCaseDto tcDto : dto.getTestCase()) {
            ProblemTestCase tc = ProblemTestCase.builder()
                    .problem(problem)
                    .isTestCase(true)
                    .input(tcDto.getInput())
                    .output(tcDto.getOutput())
                    .build();
            toSave.add(tc);
        }
        testCaseRepository.saveAll(toSave);

        return new CreateProblemResponseDto(problem.getId());
    }

    /**
     * 4) 문제 수정 (Problem + TestCase CRUD 포함)
     */
    @Transactional
    public UpdateProblemResponseDto updateProblem(Long memberId, UpdateProblemRequestDto dto) {
        // 4-1) 문제 존재 여부 및 소유권 검사
        Problem problem = problemRepository.findById(dto.getProblemId())
                .orElseThrow(() -> new CustomException(
                        ErrorCode.PROBLEM_NOT_FOUND,
                        "Problem (id=" + dto.getProblemId() + ") not found"
                ));

        if (!Objects.equals(problem.getMember().getId(), memberId)) {
            throw new CustomException(ErrorCode.FORBIDDEN, "Permission denied");
        }

        // 4-2) 문제 본문 필드 업데이트
        problem.setTitle(dto.getTitle());
        problem.setDetail(dto.getDetail());
        problem.setCategory(Enum.valueOf(
                com.example.shieldus.entity.problem.enumration.ProblemCategoryEnum.class,
                dto.getCategory().toUpperCase()
        ));
        problem.setLevel(dto.getLevel());
        problemRepository.save(problem);

        // 4-3) 기존 TestCase 모두 조회 후 Map으로 관리
        List<ProblemTestCase> existing = testCaseRepository.findByProblem(problem);
        Map<Long, ProblemTestCase> existingMap = new HashMap<>();
        for (ProblemTestCase tc : existing) {
            existingMap.put(tc.getId(), tc);
        }

        // 4-4) 요청받은 testCase 목록 순회하며: 수정/추가
        Set<Long> receivedIds = new HashSet<>();
        for (UpdateProblemRequestDto.TestCaseDto tcDto : dto.getTestCase()) {
            if (tcDto.getTestCaseId() != null) {
                // (a) 기존 케이스 수정
                ProblemTestCase toUpdate = existingMap.get(tcDto.getTestCaseId());
                if (toUpdate == null) {
                    throw new CustomException(
                            ErrorCode.PROBLEM_NOT_FOUND,
                            "TestCase not found: " + tcDto.getTestCaseId()
                    );
                }
                toUpdate.setInput(tcDto.getInput());
                toUpdate.setOutput(tcDto.getOutput());
                testCaseRepository.save(toUpdate);
                receivedIds.add(tcDto.getTestCaseId());
            } else {
                // (b) 새 케이스 추가
                ProblemTestCase newTc = ProblemTestCase.builder()
                        .problem(problem)
                        .isTestCase(true)
                        .input(tcDto.getInput())
                        .output(tcDto.getOutput())
                        .build();
                testCaseRepository.save(newTc);
            }
        }

        // 4-5) 요청받지 않은(삭제된) 케이스는 삭제
        for (ProblemTestCase oldTc : existing) {
            if (!receivedIds.contains(oldTc.getId())) {
                testCaseRepository.delete(oldTc);
            }
        }

        return new UpdateProblemResponseDto(problem.getId());
    }

    /**
     * 5) 문제 삭제
     */
    @Transactional
    public void deleteProblem(Long memberId, Long problemId) {
        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(() -> new CustomException(
                        ErrorCode.PROBLEM_NOT_FOUND,
                        "Problem (id=" + problemId + ") not found"
                ));

        if (!Objects.equals(problem.getMember().getId(), memberId)) {
            throw new CustomException(ErrorCode.FORBIDDEN, "Permission denied");
        }

        problemRepository.delete(problem);
    }

    /**
     * 6) 해당 문제에 대한 풀이(Submission) 리스트 조회
     */
    @Transactional(readOnly = true)
    public List<String> getProblemSolvedList(Long problemId) {
        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(() -> new CustomException(
                        ErrorCode.PROBLEM_NOT_FOUND,
                        "Problem (id=" + problemId + ") not found"
                ));

        List<MemberSubmitProblem> solvedList = submitProblemRepository.findByProblemAndPassTrue(problem);

        List<String> result = new ArrayList<>();
        for (MemberSubmitProblem sub : solvedList) {
            String entry = sub.getMember().getName() + " (" + sub.getCompleteDate() + ")";
            result.add(entry);
        }
        return result;
    }

    /**
     * 7) 해당 문제의 테스트 케이스만 따로 조회
     */
    @Transactional(readOnly = true)
    public List<ProblemDetailDto.TestCaseInfoDto> getTestCasesOfProblem(Long problemId) {
        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(() -> new CustomException(
                        ErrorCode.PROBLEM_NOT_FOUND,
                        "Problem (id=" + problemId + ") not found"
                ));

        List<ProblemTestCase> tcs = testCaseRepository.findByProblem(problem);
        List<ProblemDetailDto.TestCaseInfoDto> dtoList = new ArrayList<>();
        for (ProblemTestCase tc : tcs) {
            dtoList.add(ProblemDetailDto.TestCaseInfoDto.builder()
                    .id(tc.getId())
                    .input(tc.getInput())
                    .output(tc.getOutput())
                    .build());
        }
        return dtoList;
    }
}