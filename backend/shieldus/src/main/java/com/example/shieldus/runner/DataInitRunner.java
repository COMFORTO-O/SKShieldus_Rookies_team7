package com.example.shieldus.runner;

import com.example.shieldus.entity.member.Member;
import com.example.shieldus.entity.member.MemberSubmitProblem;
import com.example.shieldus.entity.member.MemberTempCode;
import com.example.shieldus.entity.member.enumration.MemberRoleEnum;
import com.example.shieldus.entity.problem.Problem;
import com.example.shieldus.entity.problem.ProblemCode;
import com.example.shieldus.entity.problem.ProblemTestCase;
import com.example.shieldus.entity.member.enumration.MemberTempCodeStatusEnum;
import com.example.shieldus.entity.problem.enumration.ProblemLanguageEnum;
import com.example.shieldus.repository.member.MemberSubmitProblemRepository;
import com.example.shieldus.repository.member.MemberTempCodeRepository;
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

import java.time.LocalDateTime;
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
    private final MemberSubmitProblemRepository memberSubmitProblemRepository;
    private final MemberTempCodeRepository memberTempCodeRepository;
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
        MemberSubmitProblem submit1 = MemberSubmitProblem.builder()
                .member(member)
                .problem(problem1)
                .pass(true)
                .createdAt(LocalDateTime.now().minusMinutes(10))
                .updatedAt(LocalDateTime.now().minusMinutes(5))
                .completedAt(LocalDateTime.now().minusMinutes(5))
                .build();

        MemberSubmitProblem submit2 = MemberSubmitProblem.builder()
                .member(member)
                .problem(problem2)
                .pass(false)
                .createdAt(LocalDateTime.now().minusMinutes(8))
                .updatedAt(LocalDateTime.now().minusMinutes(3))
                .completedAt(null)
                .build();
        memberSubmitProblemRepository.saveAll(List.of(submit1, submit2));
        MemberTempCode tempCode1 = MemberTempCode.builder()
                .memberSubmitProblem(submit1)
                .status(MemberTempCodeStatusEnum.CORRECT)
                .langauge("Python")
                .code("a, b = map(int, input().split())\nprint(a + b)")
                .submitDate(LocalDateTime.now().minusMinutes(9))
                .build();


        MemberTempCode tempCode2 = MemberTempCode.builder()
                .memberSubmitProblem(submit2)
                .status(MemberTempCodeStatusEnum.TEST)
                .langauge("Python")
                .code("a, b, c = map(int, input().split())\nprint(a * b * c)")
                .submitDate(LocalDateTime.now().minusMinutes(7))
                .build();

        MemberTempCode tempCode3 = MemberTempCode.builder()
                .memberSubmitProblem(submit1)
                .status(MemberTempCodeStatusEnum.CORRECT)
                .langauge("Python")
                .code("a, b = map(int, input().split())\nprint(a + b)\n testtest")
                .submitDate(LocalDateTime.now().minusMinutes(9))
                .build();

        memberSubmitProblemRepository.saveAll(List.of(submit1, submit2));

        submit1.setMemberTempCodes(List.of(tempCode1,tempCode3));
        submit2.setMemberTempCodes(List.of(tempCode2));
        memberTempCodeRepository.saveAll(List.of(tempCode1, tempCode2,tempCode3));

        // 테스트용 문제 10개 생성
        for (int i = 1; i <= 10; i++) {
            Problem dynamicProblem = Problem.builder()
                    .member(member)
                    .title("문제 " + i)
                    .detail("이것은 문제 " + i + "의 설명입니다.")
                    .category(problemCode1)
                    .level(i % 3 + 1)
                    .build();
            problemRepository.save(dynamicProblem);

            // 테스트케이스 3개씩
            for (int j = 1; j <= 3; j++) {
                testCaseRepository.save(ProblemTestCase.builder()
                        .problem(dynamicProblem)
                        .input(j + " " + j)
                        .output(String.valueOf(j + j))
                        .isTestCase(true)
                        .build());
            }

            // 제출내역 5개
            for (int k = 1; k <= 5; k++) {
                MemberSubmitProblem submit = MemberSubmitProblem.builder()
                        .member(member)
                        .problem(dynamicProblem)
                        .pass(k % 2 == 0) // 짝수는 통과, 홀수는 실패
                        .createdAt(LocalDateTime.now().minusMinutes(i * 10 + k))
                        .updatedAt(LocalDateTime.now().minusMinutes(i * 10 + k - 1))
                        .completedAt(k % 2 == 0 ? LocalDateTime.now().minusMinutes(i * 10 + k - 1) : null)
                        .build();
                memberSubmitProblemRepository.save(submit);

                // 제출 코드 2개
                MemberTempCode temp1 = MemberTempCode.builder()
                        .memberSubmitProblem(submit)
                        .status(k % 2 == 0 ? MemberTempCodeStatusEnum.CORRECT : MemberTempCodeStatusEnum.TEST)
                        .langauge("Python")
                        .code("print(" + i + " + " + k + ")")
                        .submitDate(LocalDateTime.now().minusMinutes(i * 10 + k - 2))
                        .build();

                MemberTempCode temp2 = MemberTempCode.builder()
                        .memberSubmitProblem(submit)
                        .status(MemberTempCodeStatusEnum.TEST)
                        .langauge("Python")
                        .code("print(" + i + " + " + k + " + ' 다시 도전')")
                        .submitDate(LocalDateTime.now().minusMinutes(i * 10 + k - 1))
                        .build();

                memberTempCodeRepository.saveAll(List.of(temp1, temp2));
            }
        }

        log.info("Test data initialization complete.");
    }
}
