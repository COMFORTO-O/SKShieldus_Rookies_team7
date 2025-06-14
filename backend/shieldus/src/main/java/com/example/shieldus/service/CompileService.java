package com.example.shieldus.service;

import com.example.shieldus.config.judge0.JudgeProperties;
import com.example.shieldus.controller.dto.CompileRequestDto;
import com.example.shieldus.controller.dto.CompileResponseDto;
import com.example.shieldus.controller.dto.CompileResponseDto.TestCaseResult;
import com.example.shieldus.entity.member.*;
import com.example.shieldus.entity.member.enumration.MemberTempCodeStatusEnum;
import com.example.shieldus.entity.problem.Problem;
import com.example.shieldus.entity.problem.ProblemTestCase;

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

    private final JudgeProperties judgeProperties;

    //Test 3개만
    public CompileResponseDto runTest(CompileRequestDto requestDto, Long memberId) {
        //문제 가져오기
        Problem problem = problemRepository.findById(requestDto.getProblemId())
                .orElseThrow(() -> new RuntimeException("문제 없음"));

        //멤버 가져오기 인증 로직 추가
        Member member = getCurrentMember(memberId);

        //제출 내역 or Test 내역 있으면 가져오기
        MemberSubmitProblem submit = getOrCreateSubmit(member, problem);

        List<ProblemTestCase> testCases = problemTestCaseRepository.findByProblem_IdAndIsTestCaseIsTrue(problem.getId());

        List<TestCaseResult> results = new ArrayList<>();

        int passed = 0;
        int total = testCases.size();

        for (int i = 0; i < total; i++) {
            ProblemTestCase testCase = testCases.get(i);
            TestCaseResult result = runJudge0Test(requestDto.getCode(), requestDto.getLanguage(), testCase);
            if (result.isCorrect()) passed++;
            results.add(result);
        }
        submit.setUpdatedAt(LocalDateTime.now());
        memberTempCodeRepository.save(MemberTempCode.builder()
                .memberSubmitProblem(submit)
                .status(MemberTempCodeStatusEnum.TEST)
                .langauge(requestDto.getLanguage())
                .code(requestDto.getCode())
                .submitDate(LocalDateTime.now())
                .build());
        System.out.println(requestDto.getCode()+"asdasd");
        return CompileResponseDto.builder()
                .passedCount(passed)
                .totalCount(total)
                .testCaseResults(results)
                .build();
    }

    public CompileResponseDto runScore(CompileRequestDto requestDto, Long memberId) {
        // 문제 가져오기
        Problem problem = problemRepository.findById(requestDto.getProblemId())
                .orElseThrow(() -> new RuntimeException("문제 없음"));
        // 멤버 가져오기 로직 추가
        Member member = getCurrentMember(memberId);

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
                .langauge(requestDto.getLanguage())
                .code(requestDto.getCode())
                .submitDate(LocalDateTime.now())
                .build());
        submit.setUpdatedAt(LocalDateTime.now());
        int plus=0;
        if (allPass) {
            submit.setPass(true);
            submit.setCompletedAt(LocalDateTime.now());
            memberSubmitProblemRepository.save(submit);

            int score = member.getMemberRank();
            int level = problem.getLevel();
            switch (level){
                case 0:
                    score+=2;
                    plus=2;
                    break;
                case 1:
                    score+=4;
                    plus=4;
                    break;
                case 2:
                    score+=6;
                    plus=6;
                    break;
                case 3:
                    score+=8;
                    plus=8;
                    break;
                case 4:
                    score+=10;
                    plus=10;
                    break;
                case 5:
                    score+=12;
                    plus=12;
                    break;
                default:
                    System.out.println("레벨 없음");
                    break;
            }
            member.setMemberRank(score);
            memberRepository.save(member);
        }

        return CompileResponseDto.builder()
                .passedCount(passed)
                .totalCount(testCases.size())
                .score(plus)
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
            ResponseEntity<String> response = restTemplate.postForEntity(judgeProperties.getUrl(), entity, String.class);
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
                                .createdAt(LocalDateTime.now())
                                .pass(false)
                                .build()));
    }

    private Member getCurrentMember(Long id) {

        return memberRepository.findById(id).orElseThrow();
    }
}

