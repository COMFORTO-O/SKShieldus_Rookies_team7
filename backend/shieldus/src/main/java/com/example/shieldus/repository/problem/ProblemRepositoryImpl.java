package com.example.shieldus.repository.problem;

import com.example.shieldus.controller.dto.ProblemResponseDto;
import com.example.shieldus.entity.member.QMember;
import com.example.shieldus.entity.member.QMemberSubmitProblem;
import com.example.shieldus.entity.problem.QProblem;
import com.example.shieldus.entity.problem.enumration.ProblemCategoryEnum;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions; // Expressions.FALSE 사용을 위해 추가
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

@Repository
public class ProblemRepositoryImpl implements ProblemRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Autowired
    public ProblemRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<ProblemResponseDto> findProblemsWithFilters(
            Long memberId,
            String category,
            Integer level,
            String title,
            String status,
            Pageable pageable) {

        QProblem p = QProblem.problem;
        QMember problemAuthor = QMember.member; // 문제 작성자 (p.member와 조인)
        QMemberSubmitProblem msp = QMemberSubmitProblem.memberSubmitProblem;

        // 공통 필터 조건
        BooleanExpression categoryCondition = hasCategory(category);
        BooleanExpression levelCondition = hasLevel(level);
        BooleanExpression titleCondition = hasTitle(title);

        // memberId 존재 여부에 따라 달라지는 조건들
        BooleanExpression memberSpecificMspJoinPredicate = null;
        BooleanExpression statusFilterCondition; // 최종 WHERE 절에 들어갈 status 조건
        com.querydsl.core.types.Expression<Boolean> isSolvedExpressionInSelect; // SELECT 절의 해결 여부

        if (memberId != null) {
            // memberId가 있을 경우: 특정 사용자의 제출 정보와 연관됨
            memberSpecificMspJoinPredicate = msp.member.id.eq(memberId); // eq(null) 방지
            statusFilterCondition = hasStatusForMember(status, msp); // 특정 멤버의 해결 상태
            isSolvedExpressionInSelect = msp.pass.coalesce(false);
        } else {
            // memberId가 null일 경우: 특정 사용자의 제출 정보와 무관
            // memberSpecificMspJoinPredicate는 null로 유지 (msp.member.id 조건 없이 조인)
            statusFilterCondition = hasStatusGeneric(status, p, msp); // 일반적인 문제 상태 (예: 아무도 풀지 않은 문제)
            isSolvedExpressionInSelect = Expressions.FALSE; // 특정 사용자가 없으므로 기본값 false
        }

        // msp와의 LEFT JOIN을 위한 최종 ON 조건 구성
        // 항상 msp.problem.eq(p)는 기본 조인 조건임
        BooleanExpression finalMspOnCondition = msp.problem.eq(p);
        if (memberSpecificMspJoinPredicate != null) {
            finalMspOnCondition = finalMspOnCondition.and(memberSpecificMspJoinPredicate);
        }

        // ✅ 1) Content 쿼리
        List<ProblemResponseDto> content = queryFactory
                .select(Projections.constructor(
                        ProblemResponseDto.class,
                        p.id,
                        p.title,
                        p.detail,
                        p.category,
                        p.level,
                        problemAuthor.name, // 문제 작성자 이름
                        isSolvedExpressionInSelect // 동적으로 설정된 해결 여부 표현식
                ))
                .from(p)
                .leftJoin(p.member, problemAuthor) // 문제와 작성자(author) 조인
                .leftJoin(msp).on(finalMspOnCondition) // 동적으로 구성된 ON 조건으로 msp 조인
                .where(categoryCondition, levelCondition, titleCondition, statusFilterCondition)
                .orderBy(p.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // ✅ 2) Count 쿼리
        // Content 쿼리와 동일한 조인 조건 및 WHERE 조건을 사용해야 정확한 개수가 나옴
        // 주의: Count 쿼리에서 msp와의 조인이 필요 없는 경우도 있지만,
        // statusFilterCondition이 msp를 참조하면 조인이 필요함.
        // 여기서는 statusFilterCondition이 msp를 사용할 수 있으므로 조인 유지.
        long total = queryFactory
                .select(p.count())
                .from(p)
                .leftJoin(p.member, problemAuthor) // 동일한 조인
                .leftJoin(msp).on(finalMspOnCondition) // 동일한 동적 ON 조건
                .where(categoryCondition, levelCondition, titleCondition, statusFilterCondition) // 동일한 WHERE 조건
                .fetchOne();

        return new PageImpl<>(
                content,
                PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("id").descending()),
                total
        );
    }

    private BooleanExpression hasCategory(String category) {
        if (!StringUtils.hasText(category)) return null;
        try {
            return QProblem.problem.category.eq(
                    ProblemCategoryEnum.valueOf(category.toUpperCase())
            );
        } catch (IllegalArgumentException e) {
            return null; // 잘못된 카테고리 문자열이면 필터링 안 함
        }
    }

    private BooleanExpression hasLevel(Integer level) {
        // .eq(null)을 피하기 위해 level이 null이면 null 반환
        return level == null ? null : QProblem.problem.level.eq(level);
    }

    private BooleanExpression hasTitle(String title) {
        return !StringUtils.hasText(title)
                ? null
                : QProblem.problem.title.containsIgnoreCase(title);
    }

    // memberId가 있을 때 사용되는 상태 필터 (특정 멤버 기준)
    private BooleanExpression hasStatusForMember(String status, QMemberSubmitProblem msp) {
        if (!StringUtils.hasText(status)) {
            return null;
        }
        if ("solved".equalsIgnoreCase(status)) {
            return msp.pass.isTrue();
        } else if ("unsolved".equalsIgnoreCase(status)) {
            // 해당 사용자가 풀지 않았거나(pass=false), 아예 제출 기록이 없는 경우(msp.id is null)
            // msp.id.isNull()은 left join된 msp 레코드가 없는 경우를 의미
            return msp.pass.isFalse().or(msp.id.isNull());
        }
        return null;
    }

    // memberId가 null일 때 사용될 수 있는 일반적인 상태 필터 (선택적 구현)
    // 예: "unsolved"가 "아무도 풀지 않은 문제"를 의미한다면
    private BooleanExpression hasStatusGeneric(String status, QProblem p, QMemberSubmitProblem msp) {
        if (!StringUtils.hasText(status)) {
            return null;
        }
        // 이 부분은 요구사항에 따라 다르게 구현될 수 있습니다.
        // 예를 들어, "unsolved"가 (어떤 사용자든) 한 번도 풀리지 않은 문제를 의미한다면,
        // 이는 서브쿼리나 다른 방식의 집계가 필요할 수 있습니다.
        // 여기서는 간단히 null을 반환하거나, memberId가 없을 때는 status 필터를 적용하지 않는 것으로 간주합니다.
        // 만약 "solved"가 "누군가 한 명이라도 푼 문제"를 의미한다면 그것도 다른 로직이 필요합니다.
        // 지금은 memberId가 없을 때 status 필터가 "의미 없음"으로 간주하고 null을 반환하는 예시입니다.
        if ("unsolved".equalsIgnoreCase(status)) {
            // 예시: 이 문제가 msp 테이블에 pass=true인 레코드가 하나도 없는 경우 (더 복잡한 쿼리 필요)
            // return JPAExpressions.selectOne()
            //    .from(msp)
            //    .where(msp.problem.eq(p).and(msp.pass.isTrue()))
            //    .notExists();
            return null; // 간단하게 처리하기 위해 null 반환 (요구사항에 따라 수정)
        } else if ("solved".equalsIgnoreCase(status)) {
            // 예시: 이 문제가 msp 테이블에 pass=true인 레코드가 하나라도 있는 경우 (더 복잡한 쿼리 필요)
            // return JPAExpressions.selectOne()
            //    .from(msp)
            //    .where(msp.problem.eq(p).and(msp.pass.isTrue()))
            //    .exists();
            return null; // 간단하게 처리하기 위해 null 반환 (요구사항에 따라 수정)
        }
        return null;
    }
}