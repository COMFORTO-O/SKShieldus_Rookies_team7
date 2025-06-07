// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// 변경 요약 (S7-32 → S7-33)
// 1) getFilteredProblems: 기존 그대로 사용 (memberId null 허용 로직 내부 처리 완료)
// 2) getProblemDetail: 시그니처 및 반환 타입 ProblemDetailDto로 변경
// 3) createProblem: 반환 타입 CreateProblemResponseDto 추가, DTO 타입 CreateProblemRequestDto 사용
// 4) updateProblem: 반환 타입 UpdateProblemResponseDto, DTO 타입 UpdateProblemRequestDto
// 5) deleteProblem: 파라미터 순서 (memberId, problemId)로 변경
// 6) getProblemSolvedList: 반환 타입 List<ProblemDetailDto.SolutionInfoDto>로 구현
// 7) getTestCasesOfProblem: 반환 타입 List<ProblemDetailDto.TestCaseInfoDto>로 구현
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

package com.example.shieldus.service.problem;

import com.example.shieldus.controller.dto.*;
import com.example.shieldus.entity.member.Member;
import com.example.shieldus.entity.member.MemberSubmitProblem;
import com.example.shieldus.entity.problem.Problem;
import com.example.shieldus.entity.problem.ProblemCode;
import com.example.shieldus.entity.problem.ProblemTestCase;
import com.example.shieldus.exception.CustomException;
import com.example.shieldus.exception.ErrorCode;
import com.example.shieldus.repository.member.MemberRepository;
import com.example.shieldus.repository.member.MemberSubmitProblemRepository;
import com.example.shieldus.repository.problem.ProblemCodeRepository;
import com.example.shieldus.repository.problem.ProblemRepository;
import com.example.shieldus.repository.problem.ProblemTestCaseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProblemService {

    private final ProblemRepository problemRepository;
    private final ProblemTestCaseRepository testCaseRepository;
    private final MemberSubmitProblemRepository submitProblemRepository;
    private final MemberRepository memberRepository;
    private final MemberSubmitProblemRepository memberSubmitProblemRepository; // 추가
    private final ProblemCodeRepository problemCodeRepository;

    /*
    * 카테고리 조회
    * */
    public List<ProblemCode> getProblemCodes() {
        return problemCodeRepository.findAll();
    }

    /*
    * 카테고리 생성
    * */
    @Transactional
    public ProblemCodeResponseDto createProblemCode(ProblemCodeRequestDto dto) {
        ProblemCode problemCode = new ProblemCode(dto.getCode(), dto.getDescription());
        problemCodeRepository.save(problemCode);
        return new ProblemCodeResponseDto(problemCode.getId(), problemCode.getCode(), problemCode.getDescription());
    }

    /*
     * 카테고리 생성
     * */
    @Transactional
    public ProblemCodeResponseDto updateProblemCode(ProblemCodeRequestDto dto) {
        ProblemCode problemCode = problemCodeRepository.findById(dto.getId()).orElseThrow(()-> new CustomException(ErrorCode.PROBLEM_CODE_NOT_FOUND));
        problemCode.setCode(dto.getCode());
        problemCode.setDescription(dto.getDescription());
        return new ProblemCodeResponseDto(problemCode.getId(), problemCode.getCode(), problemCode.getDescription());
    }





    /**
     * 1) 조건별 문제 목록 조회. 
     *    memberId가 null인 경우 solved/unsolved 상태 필터는 항상 false 처리됨.
     */
    public Page<ProblemResponseDto> getFilteredProblems(
            Long memberId, String category, Integer level, String title, Boolean solved, Pageable pageable) {
        try {
            return problemRepository.findProblemsWithFilters(memberId, category, level, title, solved, pageable);
        } catch (DataAccessException e) {
            log.error("Database error in getFilteredProblems", e);
            throw new CustomException(ErrorCode.DATABASE_ERROR, e);
        }
    }

    /**
     * 2) 문제 상세 조회 (테스트케이스 포함 + solved 여부 포함)
     * TODO : submit problem 없을 시 추가
     */
    @Transactional(readOnly = true)
    public ProblemDetailDto getProblemDetail(Long memberId, Long problemId) {

        Problem problem = problemRepository.findById(problemId).orElseThrow(() -> new CustomException(ErrorCode.PROBLEM_NOT_FOUND));
        MemberSubmitProblem submitProblem = submitProblemRepository.findByMemberIdAndProblem_Id(memberId, problemId).orElse(null);
        try{
            return new ProblemDetailDto(problem, submitProblem);
        }catch (Exception e){
            throw new CustomException(ErrorCode.DATABASE_ERROR, e);
        }

    }

    /**
     * 3) 문제 생성 (Problem + TestCase를 함께 저장)
     */
    @Transactional
    public CreateProblemResponseDto createProblem(Long memberId, CreateProblemRequestDto dto) {
        // 회원 존재 여부 체크
        Member writer = memberRepository.findByIdAndIsDeletedIsFalse(memberId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        // Category 조회
        ProblemCode category = problemCodeRepository.findByCode(dto.getCategory()).orElseThrow(() -> new CustomException(ErrorCode.PROBLEM_CODE_NOT_FOUND));
        // Problem 엔티티 생성 및 저장
        Problem problem = Problem.builder()
                .member(writer)
                .title(dto.getTitle())
                .detail(dto.getDetail())
                .category(category)
                .level(dto.getLevel())
                .build();
        problemRepository.save(problem);

        // TestCase 엔티티 목록 생성 및 저장
        List<ProblemTestCase> toSave = dto.getTestCase().stream().map(testCase -> testCase.toEntity(problem)).toList();
        testCaseRepository.saveAll(toSave);
        return new CreateProblemResponseDto(problem.getId());
    }

    /**
     * 4) 문제 수정 (Problem + TestCase CRUD 포함)
     */
    @Transactional
    public UpdateProblemResponseDto updateProblem(UpdateProblemRequestDto dto) {
        // 4-1) 문제 존재 여부 및 소유권 검사
        Problem problem = problemRepository.findByIdAndIsDeletedIsFalse(dto.getProblemId()).orElseThrow(() -> new CustomException(ErrorCode.PROBLEM_NOT_FOUND));
        ProblemCode category = problemCodeRepository.findByCode(dto.getCategory()).orElseThrow(() -> new CustomException(ErrorCode.PROBLEM_CODE_NOT_FOUND));
        // 4-2) 문제 본문 필드 업데이트
        problem.update(dto, category);

        // 4-3) 기존 TestCase 모두 조회 후 Map으로 관리
        List<ProblemTestCase> existTestCases = problem.getTestCases();
        Map<Long, ProblemTestCase> existTestCaseMap = existTestCases.stream().collect(Collectors.toMap(ProblemTestCase::getId, tc -> tc));
        List<ProblemTestCase> addTestCases = new ArrayList<>();

        for(UpdateProblemRequestDto.TestCaseDto testCaseDto : dto.getTestCase()) {
            // (a) 새 케이스 추가
            if(testCaseDto.isNullId()){
                addTestCases.add(new ProblemTestCase(problem, testCaseDto.getInput(), testCaseDto.getOutput(), testCaseDto.getIsTestCase()));
            }
            // (b) 기존 케이스 수정
            else{
                ProblemTestCase toUpdate = existTestCaseMap.get(testCaseDto.getTestCaseId());
                if(toUpdate == null){ throw new CustomException(ErrorCode.PROBLEM_NOT_FOUND); }
                toUpdate.update(testCaseDto);
            }

        }
        // 새 테스트케이스 추가
        testCaseRepository.saveAll(addTestCases);
        return new UpdateProblemResponseDto(problem.getId());
    }

    /**
     * 5) 문제 삭제 ( 실제 삭제 x, delete 변수 삭제 o )
     */
    @Transactional
    public void deleteProblem(Long memberId, Long problemId) {
        Problem problem = problemRepository.findById(problemId).orElseThrow(() -> new CustomException(ErrorCode.PROBLEM_NOT_FOUND));
        if (!Objects.equals(problem.getMember().getId(), memberId)) {throw new CustomException(ErrorCode.FORBIDDEN);}
        problem.delete();

    }

    /**
     * 6) 해당 문제에 대한 풀이(Submission) 리스트 조회
     */
    @Transactional(readOnly = true)
    public List<ProblemDetailDto.SolutionInfoDto> getProblemSolvedList(Long problemId) {
        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(() -> new CustomException(ErrorCode.PROBLEM_NOT_FOUND));

        List<MemberSubmitProblem> solvedList = submitProblemRepository.findByProblemAndPassTrue(problem);

        List<ProblemDetailDto.SolutionInfoDto> result = new ArrayList<>();
        for (MemberSubmitProblem sub : solvedList) {
            result.add(ProblemDetailDto.SolutionInfoDto.builder()
                    .memberName(sub.getMember().getName())
                    .completedAt(sub.getCompletedAt())
                    .build());
        }
        return result;
    }

    /**
     * 7) 해당 문제의 테스트 케이스만 따로 조회
     */
    @Transactional(readOnly = true)
    public List<ProblemDetailDto.TestCaseInfoDto> getTestCasesOfProblem(Long problemId) {
        problemRepository.findById(problemId).orElseThrow(() -> new CustomException(ErrorCode.PROBLEM_NOT_FOUND));
        List<ProblemTestCase> tcs = testCaseRepository.findByProblem_Id(problemId);
        return tcs.stream().map(ProblemDetailDto.TestCaseInfoDto::fromEntity).collect(Collectors.toList());
    }
}