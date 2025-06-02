package com.example.shieldus.repository.problem;

import com.example.shieldus.controller.dto.ProblemResponseDto;
import com.example.shieldus.entity.member.QMember;
import com.example.shieldus.entity.member.QMemberSubmitProblem;
import com.example.shieldus.entity.problem.QProblem;
import com.example.shieldus.entity.problem.enumration.ProblemCategoryEnum;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.Projections;
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
        QMember m = QMember.member;
        QMemberSubmitProblem msp = QMemberSubmitProblem.memberSubmitProblem;

        // 동적 검색 조건 빌드
        BooleanExpression categoryCondition = hasCategory(category);
        BooleanExpression levelCondition    = hasLevel(level);
        BooleanExpression titleCondition    = hasTitle(title);
        BooleanExpression statusCondition   = hasStatus(status, msp);

        // 1) Content 데이터 조회
        List<ProblemResponseDto> content = queryFactory
            .select(Projections.constructor(
                ProblemResponseDto.class,
                p.id,
                p.title,
                p.detail,
                p.category,
                p.level,
                m.name,
                // memberId가 null이면 항상 false → solved=false
                msp.pass.coalesce(false)
            ))
            .from(p)
            .leftJoin(p.member, m)
            .leftJoin(msp).on(
                msp.problem.eq(p),
                memberId != null ? msp.member.id.eq(memberId) : null
            )
            .where(categoryCondition, levelCondition, titleCondition, statusCondition)
            .orderBy(p.id.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        // 2) 전체 Count 조회
        Long total = queryFactory
            .select(p.count())
            .from(p)
            .leftJoin(msp).on(
                msp.problem.eq(p),
                memberId != null ? msp.member.id.eq(memberId) : null
            )
            .where(categoryCondition, levelCondition, titleCondition, statusCondition)
            .fetchOne();
        if (total == null) total = 0L;

        return new PageImpl<>(
            content,
            PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                           Sort.by("id").descending()),
            total
        );
    }

    // category 필터 (null or 빈 문자열 → 전체)
    private BooleanExpression hasCategory(String category) {
        if (!StringUtils.hasText(category)) return null;
        try {
            return QProblem.problem.category.eq(
                ProblemCategoryEnum.valueOf(category.toUpperCase())
            );
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    // level 필터
    private BooleanExpression hasLevel(Integer level) {
        return (level == null) ? null : QProblem.problem.level.eq(level);
    }

    // title(제목) 부분 검색 (containsIgnoreCase)
    private BooleanExpression hasTitle(String title) {
        return (!StringUtils.hasText(title))
             ? null
             : QProblem.problem.title.containsIgnoreCase(title);
    }

    // status 필터: “solved” or “unsolved”
    private BooleanExpression hasStatus(String status, QMemberSubmitProblem msp) {
        if (!StringUtils.hasText(status)) {
            return null;
        }
        if ("solved".equalsIgnoreCase(status)) {
            return msp.pass.isTrue();
        } else if ("unsolved".equalsIgnoreCase(status)) {
            return msp.pass.isFalse().or(msp.id.isNull());
        }
        return null;
    }
}