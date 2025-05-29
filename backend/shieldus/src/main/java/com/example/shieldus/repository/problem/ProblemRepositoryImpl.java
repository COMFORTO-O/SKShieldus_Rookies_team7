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

        BooleanExpression categoryCondition = hasCategory(category);
        BooleanExpression levelCondition    = hasLevel(level);
        BooleanExpression titleCondition    = hasTitle(title);
        BooleanExpression statusCondition   = hasStatus(status, msp);

        // ✅ 1) Content 쿼리
        List<ProblemResponseDto> content = queryFactory
            .select(Projections.constructor(
                ProblemResponseDto.class,
                p.id,
                p.title,
                p.detail,
                p.category,
                p.level,
                m.name,
                msp.pass.coalesce(false)
            ))
            .from(p)
            .leftJoin(p.member, m) // 변경
            .leftJoin(msp).on(
                msp.problem.eq(p),
                msp.member.id.eq(memberId)
            )
            .where(categoryCondition, levelCondition, titleCondition, statusCondition)
            .orderBy(p.id.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        // ✅ 2) Count 쿼리
        long total = queryFactory
            .select(p.count())
            .from(p)
            .leftJoin(p.member, m) // 필요
            .leftJoin(msp).on(
                msp.problem.eq(p),
                msp.member.id.eq(memberId)
            )
            .where(categoryCondition, levelCondition, titleCondition, statusCondition)
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
            return null;
        }
    }

    private BooleanExpression hasLevel(Integer level) {
        return level == null ? null : QProblem.problem.level.eq(level);
    }

    private BooleanExpression hasTitle(String title) {
        return !StringUtils.hasText(title)
            ? null
            : QProblem.problem.title.containsIgnoreCase(title);
    }

    private BooleanExpression hasStatus(String status, QMemberSubmitProblem msp) {
        if ("solved".equalsIgnoreCase(status)) {
            return msp.pass.isTrue();
        } else if ("unsolved".equalsIgnoreCase(status)) {
            return msp.pass.isFalse()
                   .or(msp.id.isNull());
        }
        return null;
    }
}