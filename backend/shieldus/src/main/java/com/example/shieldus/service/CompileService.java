package com.example.shieldus.service;

import com.example.shieldus.controller.dto.CompileRequestDto;
import com.example.shieldus.controller.dto.CompileResponseDto;
import com.example.shieldus.controller.dto.CompileResponseDto.TestCaseResult;
import com.example.shieldus.entity.member.*;
import com.example.shieldus.entity.member.enumration.MemberTempCodeStatusEnum;
import com.example.shieldus.entity.problem.Problem;
import com.example.shieldus.entity.problem.ProblemTestCase;
import com.example.shieldus.exception.CustomException;
import com.example.shieldus.exception.ErrorCode;
import com.example.shieldus.repository.member.MemberRepository;
import com.example.shieldus.repository.member.MemberSubmitProblemRepository;
import com.example.shieldus.repository.member.MemberTempCodeRepository;
import com.example.shieldus.repository.problem.ProblemRepository;
import com.example.shieldus.repository.problem.ProblemTestCaseRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompileService {

    private final MemberRepository memberRepository;
    private final ProblemRepository problemRepository;
    private final MemberSubmitProblemRepository memberSubmitProblemRepository;
    private final MemberTempCodeRepository memberTempCodeRepository;
    private final ProblemTestCaseRepository problemTestCaseRepository;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    private static final String JUDGE0_URL = "http://localhost:2358/submissions?base64_encoded=false&wait=true";

    // 테스트 실행 (최종)
    public CompileResponseDto runTest(CompileRequestDto requestDto) {
        try {
            Problem problem = problemRepository.findById(requestDto.getProblemId())
                    .orElseThrow(() -> new CustomException(ErrorCode.PROBLEM_NOT_FOUND));

            Member member = getCurrentMember();
            MemberSubmitProblem submit = getOrCreateSubmit(member, problem);
            List<TestCaseResult> results = executeTests(requestDto, problem, 3); // 3개 테스트 제한

            saveTempCode(submit, requestDto.getCode(), MemberTempCodeStatusEnum.TEST);

            return CompileResponseDto.builder()
                    .passedCount(countPassedResults(results))
                    .totalCount(results.size())
                    .testCaseResults(results)
                    .build();

        } catch (DataAccessException e) {
            throw new CustomException(ErrorCode.DATABASE_ERROR, e);
        } catch (RestClientException e) {
            throw new CustomException(ErrorCode.JUDGE_SERVER_ERROR, e);
        }
    }

    // 채점 실행 (최종)
    public CompileResponseDto runScore(CompileRequestDto requestDto) {
        try {
            Problem problem = problemRepository.findById(requestDto.getProblemId())
                    .orElseThrow(() -> new CustomException(ErrorCode.PROBLEM_NOT_FOUND));

            Member member = getCurrentMember();
            MemberSubmitProblem submit = getOrCreateSubmit(member, problem);
            List<TestCaseResult> results = executeTests(requestDto, problem, Integer.MAX_VALUE); // 전체 테스트 실행

            boolean allPassed = results.stream().allMatch(TestCaseResult::isCorrect);
            saveSubmitResult(submit, requestDto.getCode(), allPassed);

            return CompileResponseDto.builder()
                    .passedCount(countPassedResults(results))
                    .totalCount(results.size())
                    .testCaseResults(results)
                    .build();

        } catch (DataAccessException e) {
            throw new CustomException(ErrorCode.DATABASE_ERROR, e);
        } catch (RestClientException e) {
            throw new CustomException(ErrorCode.JUDGE_SERVER_ERROR, e);
        }
    }

    // -- 내부 메서드 (예외 처리 포함) --
    private List<TestCaseResult> executeTests(CompileRequestDto requestDto, Problem problem, int limit) {
        List<ProblemTestCase> testCases = problemTestCaseRepository.findByProblem(problem);
        return testCases.stream()
                .limit(limit)
                .map(testCase -> {
                    try {
                        return runJudge0Test(requestDto.getCode(), requestDto.getLanguage(), testCase);
                    } catch (CustomException e) {
                        log.error("테스트 실행 실패 - 입력: {}, 에러: {}", testCase.getInput(), e.getMessage());
                        return TestCaseResult.fail(testCase, e.getMessage());
                    }
                })
                .collect(Collectors.toList());
    }

    private TestCaseResult runJudge0Test(String code, String language, ProblemTestCase testCase) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> request = Map.of(
                    "source_code", code,
                    "language_id", getLanguageId(language),
                    "stdin", testCase.getInput(),
                    "expected_output", testCase.getOutput()
            );

            ResponseEntity<String> response = restTemplate.postForEntity(
                    JUDGE0_URL,
                    new HttpEntity<>(objectMapper.writeValueAsString(request), headers),
                    String.class
            );

            JsonNode root = objectMapper.readTree(response.getBody());
            String output = root.path("stdout").asText("").trim();
            String error = root.path("stderr").asText(null);

            return TestCaseResult.builder()
                    .input(testCase.getInput())
                    .expectedOutput(testCase.getOutput())
                    .actualOutput(error == null ? output : null)
                    .isCorrect(error == null && output.equals(testCase.getOutput().trim()))
                    .error(error)
                    .build();

        } catch (JsonProcessingException e) {
            throw new CustomException(ErrorCode.JSON_PARSING_ERROR, e);
        } catch (RestClientException e) {
            throw new CustomException(ErrorCode.JUDGE_SERVER_ERROR, e);
        }
    }

    private void saveTempCode(MemberSubmitProblem submit, String code, MemberTempCodeStatusEnum status) {
        try {
            memberTempCodeRepository.save(MemberTempCode.builder()
                    .memberSubmitProblem(submit)
                    .status(status)
                    .code(code)
                    .submitDate(LocalDateTime.now())
                    .build());
        } catch (DataAccessException e) {
            throw new CustomException(ErrorCode.DATABASE_ERROR, e);
        }
    }

    private void saveSubmitResult(MemberSubmitProblem submit, String code, boolean allPassed) {
        try {
            memberTempCodeRepository.save(MemberTempCode.builder()
                    .memberSubmitProblem(submit)
                    .status(allPassed ? MemberTempCodeStatusEnum.CORRECT : MemberTempCodeStatusEnum.INCORRECT)
                    .code(code)
                    .submitDate(LocalDateTime.now())
                    .build());

            if (allPassed) {
                submit.setPass(true);
                submit.setCompleteDate(LocalDateTime.now());
                memberSubmitProblemRepository.save(submit);
            }
        } catch (DataAccessException e) {
            throw new CustomException(ErrorCode.DATABASE_ERROR, e);
        }
    }

    private int getLanguageId(String language) {
        try {
            return switch (language.toLowerCase()) {
                case "python" -> 71;
                case "java" -> 62;
                case "c" -> 50;
                default -> throw new CustomException(ErrorCode.UNSUPPORTED_LANGUAGE);
            };
        } catch (IllegalArgumentException e) {
            throw new CustomException(ErrorCode.UNSUPPORTED_LANGUAGE, e);
        }
    }

    private MemberSubmitProblem getOrCreateSubmit(Member member, Problem problem) {
        try {
            return memberSubmitProblemRepository.findByMemberAndProblem(member, problem)
                    .orElseGet(() -> memberSubmitProblemRepository.save(
                            MemberSubmitProblem.builder()
                                    .member(member)
                                    .problem(problem)
                                    .pass(false)
                                    .build()));
        } catch (DataAccessException e) {
            throw new CustomException(ErrorCode.DATABASE_ERROR, e);
        }
    }

    private Member getCurrentMember() {
        try {
            return memberRepository.findById(1L) // 실제로는 인증 정보에서 추출
                    .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        } catch (DataAccessException e) {
            throw new CustomException(ErrorCode.DATABASE_ERROR, e);
        }
    }

    private int countPassedResults(List<TestCaseResult> results) {
        return (int) results.stream().filter(TestCaseResult::isCorrect).count();
    }
}

