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
import java.util.ArrayList;
import java.util.HashMap;
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

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String JUDGE0_URL = "http://localhost:2358/submissions?base64_encoded=false&wait=true";

    //Test 3개만
    public CompileResponseDto runTest(CompileRequestDto requestDto) {
        //문제 가져오기
        Problem problem = problemRepository.findById(requestDto.getProblemId())
                .orElseThrow(() -> new RuntimeException("문제 없음"));

        //멤버 가져오기 인증 로직 추가
        Member member = getCurrentMember();

        //제출 내역 or Test 내역 있으면 가져오기
        MemberSubmitProblem submit = getOrCreateSubmit(member, problem);

        List<ProblemTestCase> testCases = problemTestCaseRepository.findByProblem(problem);

        List<TestCaseResult> results = new ArrayList<>();

        int passed = 0;
        int total = Math.min(testCases.size(), 3); // 3개 이하 오류 처리 + 3개까지

        for (int i = 0; i < total; i++) {
            ProblemTestCase testCase = testCases.get(i);
            TestCaseResult result = runJudge0Test(requestDto.getCode(), requestDto.getLanguage(), testCase);
            if (result.isCorrect()) passed++;
            results.add(result);
        }

        memberTempCodeRepository.save(MemberTempCode.builder()
                .memberSubmitProblem(submit)
                .status(MemberTempCodeStatusEnum.TEST)
                .code(requestDto.getCode())
                .submitDate(LocalDateTime.now())
                .build());

        return CompileResponseDto.builder()
                .passedCount(passed)
                .totalCount(total)
                .testCaseResults(results)
                .build();
    }

    public CompileResponseDto runScore(CompileRequestDto requestDto) {
        // 문제 가져오기
        Problem problem = problemRepository.findById(requestDto.getProblemId())
                .orElseThrow(() -> new RuntimeException("문제 없음"));
        // 멤버 가져오기 로직 추가
        Member member = getCurrentMember();

        //제출내역 없으면 넣기
        MemberSubmitProblem submit = getOrCreateSubmit(member, problem);

        //문제 답 가져오기
        List<ProblemTestCase> testCases = problemTestCaseRepository.findByProblem(problem);
        List<TestCaseResult> results = new ArrayList<>();

        int passed = 0;
        for (ProblemTestCase testCase : testCases) {
            TestCaseResult result = runJudge0Test(requestDto.getCode(), requestDto.getLanguage(), testCase);
            if (result.isCorrect()) passed++;
            results.add(result);
        }

        boolean allPass = passed == testCases.size();

        memberTempCodeRepository.save(MemberTempCode.builder()
                .memberSubmitProblem(submit)
                .status(allPass ? MemberTempCodeStatusEnum.CORRECT : MemberTempCodeStatusEnum.INCORRECT)
                .code(requestDto.getCode())
                .submitDate(LocalDateTime.now())
                .build());

        if (allPass) {
            submit.setPass(true);
            submit.setCompleteDate(LocalDateTime.now());
            memberSubmitProblemRepository.save(submit);
        }

        return CompileResponseDto.builder()
                .passedCount(passed)
                .totalCount(testCases.size())
                .testCaseResults(results)
                .build();
    }

    private TestCaseResult runJudge0Test(String sourceCode, String language, ProblemTestCase testCase) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        int languageId = getLanguageId(language);

        Map<String, Object> request = new HashMap<>();
        request.put("source_code", sourceCode);
        request.put("language_id", languageId);
        request.put("stdin", testCase.getInput());
        request.put("expected_output", testCase.getOutput());

        String body;
        try {
            body = objectMapper.writeValueAsString(request);
        } catch (Exception e) {
            return TestCaseResult.builder()
                    .input(testCase.getInput())
                    .expectedOutput(testCase.getOutput())
                    .actualOutput(null)
                    .isCorrect(false)
                    .error("JSON 직렬화 실패: " + e.getMessage())
                    .build();
        }

        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(JUDGE0_URL, entity, String.class);
            JsonNode root = objectMapper.readTree(response.getBody());

            String stdout = root.path("stdout").asText("").trim();
            String stderr = root.path("stderr").asText(null);
            String compileOutput = root.path("compile_output").asText(null);
            String message = root.path("message").asText(null);

            boolean isCorrect = stdout.equals(testCase.getOutput().trim());

            return TestCaseResult.builder()
                    .input(testCase.getInput())
                    .expectedOutput(testCase.getOutput())
                    .actualOutput(stdout)
                    .isCorrect(isCorrect)
                    .error(stderr != null ? stderr : (compileOutput != null ? compileOutput : message))
                    .build();

        } catch (Exception e) {
            return TestCaseResult.builder()
                    .input(testCase.getInput())
                    .expectedOutput(testCase.getOutput())
                    .actualOutput(null)
                    .isCorrect(false)
                    .error("Judge0 호출 실패: " + e.getMessage())
                    .build();
        }
    }


    private int getLanguageId(String language) {
        return switch (language.toLowerCase()) {
            case "python" -> 71;
            case "java" -> 62;
            case "c" -> 50;
            default -> throw new IllegalArgumentException("지원하지 않는 언어입니다.");
        };
    }

    private MemberSubmitProblem getOrCreateSubmit(Member member, Problem problem) {
        return memberSubmitProblemRepository.findByMemberAndProblem(member, problem)
                .orElseGet(() -> memberSubmitProblemRepository.save(
                        MemberSubmitProblem.builder()
                                .member(member)
                                .problem(problem)
                                .pass(false)
                                .build()));
    }

    private Member getCurrentMember() {

        return memberRepository.findById(1L).orElseThrow();
    }
}

