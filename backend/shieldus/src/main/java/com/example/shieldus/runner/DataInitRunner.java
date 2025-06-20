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
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


@Component
@Order(1)
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "spring.jpa.hibernate.ddl-auto", havingValue = "create", matchIfMissing = false)
public class DataInitRunner implements CommandLineRunner {

    private final MemberRepository memberRepository;
    private final ProblemRepository problemRepository;
    private final ProblemCodeRepository problemCodeRepository;
    private final ProblemTestCaseRepository testCaseRepository;
    private final MemberSubmitProblemRepository memberSubmitProblemRepository;
    private final MemberTempCodeRepository memberTempCodeRepository;
    private final PasswordEncoder passwordEncoder;

    // 사용자 이름을 100개로 확장
    private final String[] userKoreanNames = {
            "김민준", "이서준", "박도윤", "정하준", "조은우", "최아윤", "강지유", "장서윤", "임채원", "한수아",
            "김서준", "이도윤", "박하준", "정은우", "조아윤", "최지유", "강서윤", "장채원", "임수아", "한민준",
            "김도윤", "이하준", "박은우", "정아윤", "조지유", "최서윤", "강채원", "장수아", "임민준", "한서준",
            "김하준", "이은우", "박아윤", "정지유", "조서윤", "최채원", "강수아", "장민준", "임서준", "한도윤",
            "김은우", "이아윤", "박지유", "정서윤", "조채원", "최수아", "강민준", "장서준", "임도윤", "한하준",
            "김아윤", "이지유", "박서윤", "정채원", "조수아", "최민준", "강서준", "장도윤", "임하준", "한은우",
            "김지유", "이서윤", "박채원", "정수아", "조민준", "최서준", "강도윤", "장하준", "임은우", "한아윤",
            "김서윤", "이채원", "박수아", "정민준", "조서준", "최도윤", "강하준", "장은우", "임아윤", "한지유",
            "김채원", "이수아", "박민준", "정서준", "조도윤", "최하준", "강은우", "장아윤", "임지유", "한서윤",
            "김수아", "이민준", "박서준", "정도윤", "조하준", "최은우", "강아윤", "장지유", "임서윤", "한채원"
    };

    private final String[] userEmailPrefixes = {
            "kim.minjun", "lee.seojun", "park.doyun", "jeong.hajun", "cho.eunwoo",
            "choi.ayun", "kang.jiyu", "jang.seoyun", "im.chaewon", "han.sua",
            "kim.seojun", "lee.doyun", "park.hajun", "jeong.eunwoo", "cho.ayun",
            "choi.jiyu", "kang.seoyun", "jang.chaewon", "im.sua", "han.minjun",
            "kim.doyun", "lee.hajun", "park.eunwoo", "jeong.ayun", "cho.jiyu",
            "choi.seoyun", "kang.chaewon", "jang.sua", "im.minjun", "han.seojun",
            "kim.hajun", "lee.eunwoo", "park.ayun", "jeong.jiyu", "cho.seoyun",
            "choi.chaewon", "kang.sua", "jang.minjun", "im.seojun", "han.doyun",
            "kim.eunwoo", "lee.ayun", "park.jiyu", "jeong.seoyun", "cho.chaewon",
            "choi.sua", "kang.minjun", "jang.seojun", "im.doyun", "han.hajun",
            "kim.ayun", "lee.jiyu", "park.seoyun", "jeong.chaewon", "cho.sua",
            "choi.minjun", "kang.seojun", "jang.doyun", "im.hajun", "han.eunwoo",
            "kim.jiyu", "lee.seoyun", "park.chaewon", "jeong.sua", "cho.minjun",
            "choi.seojun", "kang.doyun", "jang.hajun", "im.eunwoo", "han.ayun",
            "kim.seoyun", "lee.chaewon", "park.sua", "jeong.minjun", "cho.seojun",
            "choi.doyun", "kang.hajun", "jang.eunwoo", "im.ayun", "han.jiyu", // 이 부분 수정됨
            "kim.chaewon", "lee.sua", "park.minjun", "jeong.seojun", "cho.doyun",
            "choi.hajun", "kang.eunwoo", "jang.ayun", "im.jiyu", "han.seoyun", // 이 부분 수정됨
            "kim.sua", "lee.minjun", "park.seojun", "jeong.doyun", "cho.hajun",
            "choi.eunwoo", "kang.ayun", "jang.jiyu", "im.seoyun", "han.chaewon"
    };

    private final String[] adminKoreanNames = {
            "이로운", "박지혜", "최준호", "정미소", "송강현"
    };

    private final String[] adminEmailPrefixes = {
            "admin.lee", "admin.park", "admin.choi", "admin.jeong", "admin.song"
    };


    @Override
    @Transactional
    public void run(String[] args) throws Exception {

        System.out.println("--- DB ddl-auto:create 감지. 더미 데이터 초기화를 시작합니다. ---");

        String userEncodedPassword = passwordEncoder.encode("test1234");
        String adminEncodedPassword = passwordEncoder.encode("admin1234");

        List<Member> membersToSave = new ArrayList<>();
        List<ProblemCode> problemCodesToSave = new ArrayList<>();
        List<Problem> problemsToSave = new ArrayList<>();
        List<MemberSubmitProblem> submitsToSave = new ArrayList<>();

        Random random = new Random();

        /*
         * 1. 일반 회원 객체 생성 및 리스트에 추가
         * */
        System.out.println("--- 일반 회원 객체 생성 시작 ---");
        for (int i = 0; i < userKoreanNames.length; i++) {
            String name = userKoreanNames[i];
            String email = userEmailPrefixes[i] + "@example.com";

            Member member = Member.builder()
                    .email(email)
                    .password(userEncodedPassword)
                    .name(name)
                    .phone("010123456" + String.format("%02d", (i + 1)))
                    .memberRank(0)
                    .role(MemberRoleEnum.USER)
                    .isDeleted(false)
                    .build();
            membersToSave.add(member);
        }
        System.out.println("생성 대기 중인 일반 회원 수: " + membersToSave.size());

        /*
         * 2. 관리자 객체 생성 및 리스트에 추가
         * */
        System.out.println("--- 관리자 객체 생성 시작 ---");
        String[] adminTeams = {"[총괄팀]", "[인사팀]", "[회계팀]", "[마케팅팀]", "[IT팀]"};
        for (int i = 0; i < adminKoreanNames.length; i++) {
            String originalAdminName = adminKoreanNames[i];
            String adminEmail = adminEmailPrefixes[i] + "@admin.com";
            String adminNameWithTeam = originalAdminName + adminTeams[i];

            Member adminMember = Member.builder()
                    .email(adminEmail)
                    .password(adminEncodedPassword)
                    .name(adminNameWithTeam)
                    .phone("0109999" + String.format("%04d", (i + 1)))
                    .memberRank(90 + i)
                    .role(MemberRoleEnum.ADMIN)
                    .isDeleted(false)
                    .build();
            membersToSave.add(adminMember);
        }
        System.out.println("생성 대기 중인 관리자 수: " + (membersToSave.size() - userKoreanNames.length));
        System.out.println("--- 총 " + membersToSave.size() + "명의 멤버 객체 생성 완료 ---");

        // 모든 멤버를 한 번에 저장
        memberRepository.saveAll(membersToSave);
        System.out.println("--- 총 " + membersToSave.size() + "명의 멤버 DB 저장 완료 ---");


        /*
         * 3. ProblemCode (카테고리) 객체 생성 및 리스트에 추가
         * */
        System.out.println("--- ProblemCode (카테고리) 객체 생성 시작 ---");
        String[] categoryDescriptions = {
                "수학", "문자열", "배열", "구현", "정렬",
                "스택/큐", "그래프", "트리", "DFS/BFS", "동적 계획법"
        };
        String[] categoryCodes = {
                "MATH", "STRING", "ARRAY", "IMPLEMENTATION", "SORTING",
                "STACK_QUEUE", "GRAPH", "TREE", "DFS_BFS", "DP"
        };

        for (int i = 0; i < categoryDescriptions.length; i++) {
            String desc = categoryDescriptions[i];
            String codeValue = categoryCodes[i];

            ProblemCode newCategory = ProblemCode.builder()
                    .code(codeValue)
                    .description(desc)
                    .build();
            problemCodesToSave.add(newCategory);
        }
        System.out.println("생성 대기 중인 ProblemCode(카테고리) 수: " + problemCodesToSave.size());

        // 모든 ProblemCode를 한 번에 저장
        problemCodeRepository.saveAll(problemCodesToSave);
        System.out.println("--- 총 " + problemCodesToSave.size() + "개의 ProblemCode(카테고리) DB 저장 완료 ---");

        // ProblemCode를 Map으로 변환하여 쉽게 접근하도록 준비 (code로 검색)
        Map<String, ProblemCode> categoryMap = problemCodesToSave.stream()
                .collect(Collectors.toMap(ProblemCode::getCode, pc -> pc));


        /*
         * 4. 문제 20개 객체 생성 및 리스트에 추가 (타이틀별 세부 정보 포함)
         * */
        System.out.println("--- 문제 20개 객체 생성 시작 ---");

        // 각 문제 타이틀에 대한 세부 정보 (난이도, 카테고리 코드, 상세 설명)를 정의
        class ProblemDetail {
            String title;
            String detail;
            int level;
            String categoryCode; // ProblemCode의 code 값

            ProblemDetail(String title, String detail, int level, String categoryCode) {
                this.title = title;
                this.detail = detail;
                this.level = level;
                this.categoryCode = categoryCode;
            }
        }

        List<ProblemDetail> predefinedProblems = new ArrayList<>();
        predefinedProblems.add(new ProblemDetail("두 수 더하기", "두 정수를 입력받아 합을 계산하는 문제입니다.", 1, "MATH"));
        predefinedProblems.add(new ProblemDetail("문자열 뒤집기", "주어진 문자열을 뒤집어서 반환하는 문제입니다.", 2, "STRING"));
        predefinedProblems.add(new ProblemDetail("최대값 찾기", "정수 배열에서 가장 큰 값을 찾는 문제입니다.", 2, "ARRAY"));
        predefinedProblems.add(new ProblemDetail("짝수 판별", "주어진 정수가 짝수인지 홀수인지 판별하는 문제입니다.", 1, "IMPLEMENTATION"));
        predefinedProblems.add(new ProblemDetail("홀수 판별", "주어진 정수가 홀수인지 짝수인지 판별하는 문제입니다.", 1, "IMPLEMENTATION"));
        predefinedProblems.add(new ProblemDetail("배열 평균", "정수 배열의 모든 요소의 평균을 계산하는 문제입니다.", 2, "ARRAY"));
        predefinedProblems.add(new ProblemDetail("문자열 포함 여부", "두 문자열이 주어졌을 때, 한 문자열이 다른 문자열에 포함되는지 확인하는 문제입니다.", 2, "STRING"));
        predefinedProblems.add(new ProblemDetail("가장 긴 단어 찾기", "주어진 문장에서 가장 길이가 긴 단어를 찾는 문제입니다.", 3, "STRING"));
        predefinedProblems.add(new ProblemDetail("피보나치 수열", "N번째 피보나치 수를 계산하는 문제입니다. (재귀 또는 반복)", 3, "DP")); // 동적 계획법
        predefinedProblems.add(new ProblemDetail("팩토리얼 계산", "N의 팩토리얼 값을 계산하는 문제입니다.", 2, "MATH"));
        predefinedProblems.add(new ProblemDetail("소수 판별", "주어진 숫자가 소수인지 판별하는 효율적인 방법을 찾는 문제입니다.", 3, "MATH"));
        predefinedProblems.add(new ProblemDetail("숫자 자리수 합", "주어진 숫자의 각 자리수를 더한 값을 계산하는 문제입니다.", 1, "MATH"));
        predefinedProblems.add(new ProblemDetail("배열 중복 제거", "정수 배열에서 중복된 요소를 제거한 새 배열을 반환하는 문제입니다.", 3, "ARRAY"));
        predefinedProblems.add(new ProblemDetail("문자열 압축", "반복되는 문자를 압축하는 알고리즘을 구현하는 문제입니다. 예: 'AAABBC' -> 'A3B2C1'", 4, "STRING"));
        predefinedProblems.add(new ProblemDetail("괄호 짝 맞추기", "주어진 문자열에 괄호가 올바르게 짝지어져 있는지 확인하는 문제입니다.", 3, "STACK_QUEUE")); // 스택/큐
        predefinedProblems.add(new ProblemDetail("최소값 찾기", "정수 배열에서 가장 작은 값을 찾는 문제입니다.", 2, "ARRAY"));
        predefinedProblems.add(new ProblemDetail("제곱근 계산", "주어진 숫자의 제곱근을 계산하는 문제입니다.", 2, "MATH"));
        predefinedProblems.add(new ProblemDetail("약수 구하기", "주어진 숫자의 모든 약수를 찾아 반환하는 문제입니다.", 2, "MATH"));
        predefinedProblems.add(new ProblemDetail("이진수 변환", "10진수를 2진수로 변환하는 문제입니다.", 3, "IMPLEMENTATION"));
        predefinedProblems.add(new ProblemDetail(
                "배열 정렬",
                """
                정수로 이루어진 배열이 주어집니다. 해당 배열을 오름차순으로 정렬한 결과를 반환하세요.
            
                ### 입력 형식
                - 첫째 줄에 정수 N이 주어집니다. (1 ≤ N ≤ 100)
                - 둘째 줄에 N개의 정수가 공백으로 구분되어 주어집니다. (-1000 ≤ 각 정수 ≤ 1000)
            
                ### 출력 형식
                - 오름차순으로 정렬된 정수들을 공백으로 출력합니다.
            
                ### 입력 예시
                5
                5 3 2 4 1
            
                ### 출력 예시
                1 2 3 4 5
                """,
                3,
                "SORTING"
        ));

        List<Member> adminMembers = membersToSave.stream()
                .filter(m -> m.getRole().equals(MemberRoleEnum.ADMIN))
                .toList();

        // 미리 정의된 문제를 모두 생성
        IntStream.range(0, predefinedProblems.size()).forEach(i -> {
            ProblemDetail p = predefinedProblems.get(i);

            Member problemCreator = null;
            if (!adminMembers.isEmpty()) {
                problemCreator = adminMembers.get(random.nextInt(adminMembers.size()));
            } else {
                problemCreator = membersToSave.get(random.nextInt(membersToSave.size()));
            }

            // 정의된 카테고리 코드를 사용하여 ProblemCode 객체를 찾아 연결
            ProblemCode category = categoryMap.get(p.categoryCode);
            if (category == null) {
                // 정의되지 않은 카테고리 코드라면 기본값으로 "구현" 카테고리를 사용 (방어 코드)
                category = categoryMap.get("IMPLEMENTATION");
                if (category == null) { // "구현"도 없다면 첫 번째 카테고리 사용
                    category = problemCodesToSave.get(0);
                }
            }

            Problem problem = Problem.builder()
                    .title(p.title) // 미리 정의된 타이틀 사용
                    .detail(p.detail) // 미리 정의된 상세 설명 사용
                    .level(p.level) // 미리 정의된 난이도 사용
                    .isDeleted(false)
                    .member(problemCreator)
                    .category(category) // problemCode로 변경
                    .build();
            problemsToSave.add(problem);
        });

        System.out.println("생성 대기 중인 문제 수: " + problemsToSave.size());

        // 모든 문제를 한 번에 저장
        problemRepository.saveAll(problemsToSave);
        System.out.println("--- 총 " + problemsToSave.size() + "개의 문제 DB 저장 완료 ---");


        /*
         * 5. 제출 내역 100개 객체 생성 및 리스트에 추가
         * */
        System.out.println("--- 제출 내역 100개 객체 생성 시작 ---");
        for (int i = 0; i < 100; i++) {
            Member randomMember = membersToSave.get(random.nextInt(membersToSave.size()));
            Problem randomProblem = problemsToSave.get(random.nextInt(problemsToSave.size()));

            boolean isCorrect = random.nextBoolean();
            boolean pass = isCorrect && random.nextBoolean(); // isCorrect가 true일 때만 pass될 가능성
            LocalDateTime completedAt = isCorrect ? LocalDateTime.now().minusMinutes(random.nextInt(60 * 24 * 30)) : null;

            MemberSubmitProblem submit = MemberSubmitProblem.builder()
                    .member(randomMember)
                    .problem(randomProblem)
                    .pass(pass)
                    .completedAt(completedAt)
                    .build();
            submitsToSave.add(submit);
        }
        System.out.println("생성 대기 중인 제출 내역 수: " + submitsToSave.size());

        List<ProblemTestCase> arraySortTestCases = List.of(
                new ProblemTestCase(null, "5\n5 3 2 4 1", "1 2 3 4 5", true),
                new ProblemTestCase(null, "3\n10 -1 3", "-1 3 10", true),
                new ProblemTestCase(null, "4\n0 0 0 0", "0 0 0 0", true),
                new ProblemTestCase(null, "2\n1000 -1000", "-1000 1000", true),
                new ProblemTestCase(null, "6\n9 8 7 6 5 4", "4 5 6 7 8 9", true),
                new ProblemTestCase(null, "10\n1 3 2 6 5 4 10 9 8 7", "1 2 3 4 5 6 7 8 9 10", false),
                new ProblemTestCase(null, "1\n42", "42", false),
                new ProblemTestCase(null, "7\n-3 -1 -2 -7 -5 -4 -6", "-7 -6 -5 -4 -3 -2 -1", false),
                new ProblemTestCase(null, "5\n100 200 100 200 100", "100 100 100 200 200", false),
                new ProblemTestCase(null, "8\n10 20 10 30 40 10 20 30", "10 10 10 20 20 30 30 40", false)
        );
        Problem arraySortProblem = problemsToSave.stream()
                .filter(p -> p.getTitle().equals("배열 정렬"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("배열 정렬 문제를 찾을 수 없습니다."));

        arraySortTestCases.forEach(tc -> tc.setProblem(arraySortProblem));

        testCaseRepository.saveAll(arraySortTestCases);
        System.out.println("--- '배열 정렬' 문제의 테스트케이스 10개 DB 저장 완료 ---");

        // 모든 제출 내역을 한 번에 저장
        memberSubmitProblemRepository.saveAll(submitsToSave);
        System.out.println("--- 총 " + submitsToSave.size() + "개의 제출 내역 DB 저장 완료 ---");

        System.out.println("--- 모든 더미 데이터 초기화 완료 ---");
    }
}

