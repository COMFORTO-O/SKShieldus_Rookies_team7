package com.example.shieldus.runner;

import com.example.shieldus.entity.member.Member;
import com.example.shieldus.entity.member.enumration.MemberRoleEnum;
import com.example.shieldus.entity.problem.Problem;
import com.example.shieldus.entity.problem.ProblemCode;
import com.example.shieldus.entity.problem.ProblemTestCase;
import com.example.shieldus.entity.problem.enumration.ProblemCategoryEnum;
import com.example.shieldus.entity.problem.enumration.ProblemLanguageEnum;
import com.example.shieldus.repository.member.MemberRepository;
import com.example.shieldus.repository.problem.ProblemCodeRepository;
import com.example.shieldus.repository.problem.ProblemRepository;
import com.example.shieldus.repository.problem.ProblemTestCaseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;


@Component
@Order(1)
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "runner.data-init", havingValue = "true", matchIfMissing = false)
public class DataInitRunner implements CommandLineRunner {

    private final MemberRepository memberRepository;
    private final ProblemRepository problemRepository;
    private final ProblemCodeRepository problemCodeRepository;
    private final ProblemTestCaseRepository testCaseRepository;
    private final PasswordEncoder passwordEncoder;
    @Override
    @Transactional
    public void run(String... args) {


//        // 회원 생성
        Member member = Member.builder()
                .email("a@a.com")
                .password(passwordEncoder.encode("123"))
                .name("테스트유저")
                .phone("01012345678")
                .memberRank(0)
                .role(MemberRoleEnum.USER)
                .isDeleted(false)
                .build();
        memberRepository.save(member);
        Member member2 = Member.builder()
                .email("b@b.com")
                .password(passwordEncoder.encode("123"))
                .name("테스트유저2")
                .phone("01012345679")
                .memberRank(0)
                .role(MemberRoleEnum.USER)
                .isDeleted(false)
                .build();
        memberRepository.save(member2);

        // 코드 생성
        ProblemCode problemCode1 = new ProblemCode("JAVA", "Java");
        ProblemCode problemCode2 = new ProblemCode("PYTHON", "Python");
        ProblemCode problemCode3 = new ProblemCode("C", "C");
        ProblemCode problemCode4 = new ProblemCode("HTML", "HTML");
        ProblemCode problemCode5 = new ProblemCode("ALGORITHM", "Algorithm");
        ProblemCode problemCode6 = new ProblemCode("string", "string");
        List<ProblemCode> codeList = Arrays.asList(problemCode1, problemCode2, problemCode3, problemCode4, problemCode5, problemCode6);
        problemCodeRepository.saveAll(codeList);
//        // 문제 1: 두 수 더하기
        Problem problem1 = Problem.builder()
                .member(member)
                .title("두 수 더하기")
                .detail("두 정수를 입력받아 합을 출력하세요")
                .category(problemCode1)
                .level(1)
                .build();
        problemRepository.save(problem1);
//
        List.of(
                new String[]{"1 2", "3"},
                new String[]{"5 7", "12"},
                new String[]{"10 20", "30"},
                new String[]{"0 0", "0"},
                new String[]{"100 200", "300"}
        ).forEach(pair -> testCaseRepository.save(
                ProblemTestCase.builder()
                        .problem(problem1)
                        .isTestCase(true)
                        .input(pair[0])
                        .output(pair[1])
                        .build()
        ));

        // 문제 2: 세 수 곱하기
        Problem problem2 = Problem.builder()
                .member(member)
                .title("세 수 곱하기")
                .detail("세 정수를 입력받아 곱을 출력하세요")
                .category(problemCode4)
                .level(2)
                .build();
        problemRepository.save(problem2);

        List.of(
                new String[]{"1 2 3", "6"},
                new String[]{"2 3 4", "24"},
                new String[]{"0 1 100", "0"},
                new String[]{"-1 2 3", "-6"},
                new String[]{"5 5 5", "125"}
        ).forEach(pair -> testCaseRepository.save(
                ProblemTestCase.builder()
                        .problem(problem2)
                        .isTestCase(true)
                        .input(pair[0])
                        .output(pair[1])
                        .build()
        ));

        log.info("Test data initialization complete.");
    }
}
