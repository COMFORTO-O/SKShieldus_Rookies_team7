package com.example.shieldus.repository.problem;

import com.example.shieldus.controller.dto.ProblemResponseDto;
import com.example.shieldus.entity.member.Member;
import com.example.shieldus.entity.member.MemberSubmitProblem;
import com.example.shieldus.entity.problem.Problem;
import com.example.shieldus.entity.problem.ProblemCode;
import com.example.shieldus.entity.problem.enumration.ProblemCategoryEnum;
import com.example.shieldus.repository.member.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import jakarta.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@Disabled
@DataJpaTest(properties = {
    "spring.jpa.show-sql=true",
    "spring.jpa.properties.hibernate.format_sql=true",
    "logging.level.org.hibernate.SQL=DEBUG",
    "logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE"
  })
@Import(ProblemRepositoryImpl.class)  // Custom 구현체를 빈으로 등록
@ActiveProfiles("test")
class ProblemRepositoryImplTest {

    @Autowired
    private EntityManager em;

    @Autowired
    private ProblemRepository problemRepository;

    @Autowired
    private ProblemCodeRepository problemCodeRepository;

    private Member memberA;
    private Problem prob1, prob2, prob3;

    @BeforeEach
    void setUp() {
        //  1) 멤버 생성
        memberA = new Member();
        memberA.setName("Alice");
        em.persist(memberA);

        Member memberB = new Member();
        memberB.setName("Bob");
        em.persist(memberB);

        ProblemCode problemCode =new ProblemCode("TEST", "test");
        //  2) 문제 3개 생성
        prob1 = new Problem();
        prob1.setTitle("Java Basics");
        prob1.setDetail("Learn Java");
        prob1.setCategory(problemCode);
        prob1.setLevel(1);
        prob1.setMember(memberA);
        em.persist(prob1);

        prob2 = new Problem();
        prob2.setTitle("Python Intro");
        prob2.setDetail("Python Basics");
        prob2.setCategory(problemCode);
        prob2.setLevel(2);
        prob2.setMember(memberA);
        em.persist(prob2);

        prob3 = new Problem();
        prob3.setTitle("Advanced Java");
        prob3.setDetail("Streams & Lambdas");
        prob3.setCategory(problemCode);
        prob3.setLevel(3);
        prob3.setMember(memberB);
        em.persist(prob3);

        //  3) memberA 가 prob1 을 solve 했다고 표시
        MemberSubmitProblem sub = new MemberSubmitProblem();
        sub.setMember(memberA);
        sub.setProblem(prob1);
        sub.setPass(true);
        em.persist(sub);

        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("필터 없이 조회하면 모든 문제가 나와야 한다")
    void testFindAllNoFilters() {
        Page<ProblemResponseDto> page =
            problemRepository.findProblemsWithFilters(
                memberA.getId(), null, null, null, null,
                PageRequest.of(0, 10, Sort.by("id").descending())
            );

        assertThat(page.getTotalElements()).isEqualTo(3);
        assertThat(page.getContent().get(0).getTitle()).isEqualTo("Advanced Java");
    }

    @Test
    @DisplayName("category=ALGORITHM 으로 필터링")
    void testFilterByCategory() {
        Page<ProblemResponseDto> page =
            problemRepository.findProblemsWithFilters(
                memberA.getId(), "algorithm", null, null, null,
                PageRequest.of(0, 10)
            );

        assertThat(page.getTotalElements()).isEqualTo(2);
        assertThat(page.getContent())
            .allSatisfy(dto -> assertThat(dto.getCategory()).isEqualTo(ProblemCategoryEnum.ALGORITHM));
    }

    @Test
    @DisplayName("level=2 로 필터링")
    void testFilterByLevel() {
        Page<ProblemResponseDto> page =
            problemRepository.findProblemsWithFilters(
                memberA.getId(), null, 2, null, null,
                PageRequest.of(0, 10)
            );

        assertThat(page.getTotalElements()).isEqualTo(1);
        assertThat(page.getContent().get(0).getTitle()).isEqualTo("Python Intro");
    }

    @Test
    @DisplayName("title 포함검색 (containsIgnoreCase)")
    void testFilterByTitle() {
        Page<ProblemResponseDto> page =
            problemRepository.findProblemsWithFilters(
                memberA.getId(), null, null, "java", null,
                PageRequest.of(0, 10)
            );

        assertThat(page.getTotalElements()).isEqualTo(2);
        assertThat(page.getContent())
            .extracting(ProblemResponseDto::getTitle)
            .allMatch(title -> title.toLowerCase().contains("java"));
    }
//
//    @Test
//    @DisplayName("status=solved 필터링")
//    void testFilterBySolved() {
//        Page<ProblemResponseDto> page =
//            problemRepository.findProblemsWithFilters(
//                memberA.getId(), null, null, null, true
//                PageRequest.of(0, 10)
//            );
//
//        assertThat(page.getTotalElements()).isEqualTo(1);
//        assertThat(page.getContent().get(0).isSolved()).isTrue();
//        assertThat(page.getContent().get(0).getTitle()).isEqualTo("Java Basics");
//    }
//
//    @Test
//    @DisplayName("status=unsolved 필터링")
//    void testFilterByUnsolved() {
//        Page<ProblemResponseDto> page =
//            problemRepository.findProblemsWithFilters(
//                memberA.getId(), null, null, null, false,
//                PageRequest.of(0, 10)
//            );
//
//        assertThat(page.getTotalElements()).isEqualTo(2);
//        assertThat(page.getContent())
//            .allSatisfy(dto -> assertThat(dto.isSolved()).isFalse());
//    }
}
