package com.example.shieldus.repository.problem;

import com.example.shieldus.controller.dto.ProblemResponseDto;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.example.shieldus.entity.member.QMemberSubmitProblem.memberSubmitProblem;
import static com.example.shieldus.entity.problem.QProblem.problem;

@Repository
@RequiredArgsConstructor
public class ProblemRepositoryImpl implements ProblemRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<ProblemResponseDto> findProblemsWithFilters(
            Long memberId,
            String category,
            Integer level,
            String title,
            Boolean solved,
            Pageable pageable) {

        List<ProblemResponseDto> problemResponses = queryFactory.select(Projections.constructor(ProblemResponseDto.class,
                        problem.id,
                        problem.title,
                        problem.detail,
                        problem.level,
                        problem.createdAt,
                        problem.category.id,
                        problem.category.code,
                        problem.category.description
                ))
                .from(problem)
                .leftJoin(problem.category)
                .where(
                        eqCategory(category),
                        eqLevel(level),
                        containsTitle(title)
                )
                .orderBy(problemOrderBy(pageable))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<Long> idList = problemResponses.stream().map(ProblemResponseDto::getId).toList();
        // 성공, 실패 비율 구하기
        List<ProblemResponseDto.Pass> passList = queryFactory.select(
                Projections.constructor(
                        ProblemResponseDto.Pass.class,
                        memberSubmitProblem.member.id,
                        memberSubmitProblem.problem.id.as("problemId"),
                        new CaseBuilder()
                                .when(memberSubmitProblem.pass.isTrue()).then(1)
                                .otherwise(0)
                                .max() // 그룹 내에서 가장 큰 값(1)을 가져옴
                                .eq(1),
                        memberSubmitProblem.completedAt
                ))
                .from(memberSubmitProblem)
                .where(memberSubmitProblem.problem.id.in(idList))
                .groupBy(memberSubmitProblem.member.id, memberSubmitProblem.problem.id)
                .fetch();

        Map<Long, Long> passCountMap = new HashMap<>();
        Map<Long, Long> successPassCountMap = new HashMap<>();
        passList.forEach(pass -> {
            passCountMap.put(pass.getProblemId(), passCountMap.getOrDefault(pass.getProblemId(), 0L) + 1);
            if (pass.getIsPass()) {
                // 패스 ID 와 같다면 pass 처리
                if(pass.getMemberId().equals(memberId)) {
                    problemResponses.stream().filter(dto->dto.getId().equals(pass.getProblemId())).findFirst().ifPresent(dto -> dto.setSolved(true));
                }
                successPassCountMap.put(pass.getProblemId(), successPassCountMap.getOrDefault(pass.getProblemId(), 0L) + 1);
            }
        });
        problemResponses.forEach(problem -> {
            // 키가 없을 겨우
            if(!passCountMap.containsKey(problem.getId())) {
               problem.setPass(0L,0L);
            }
            else{
                problem.setPass(successPassCountMap.get(problem.getId()), passCountMap.get(problem.getId()));
            }
        });



        Long total = queryFactory.select(problem.count())
                .from(problem)
                .leftJoin(problem.category)
                .where(
                        eqCategory(category),
                        eqLevel(level),
                        containsTitle(title)
                )
                .fetchOne();
        if (total == null) {
            total = 0L;
        }
        return new PageImpl<>(problemResponses, pageable, total);
    }

    private BooleanExpression eqCategory(String category) {
        return (category == null || category.isEmpty()) ? null : problem.category.code.eq(category);
    }

    private BooleanExpression eqLevel(Integer level) {
        return level == null ? null : problem.level.eq(level);
    }

    private BooleanExpression containsTitle(String title) {
        return (title == null || title.isEmpty()) ? null : problem.title.containsIgnoreCase(title);
    }

    private BooleanExpression eqStatus(Long memberId, Boolean solved) {
        return memberId != null && memberId > 0 && solved != null ? (solved ? memberSubmitProblem.pass.isTrue() : memberSubmitProblem.pass.isFalse()) : null;
    }

    private OrderSpecifier<?>[] problemOrderBy(Pageable pageable) {
        if (pageable.getSort().isSorted()) {
            return pageable.getSort().stream().map(order -> {
                Order direction = order.isAscending() ? Order.ASC : Order.DESC;
                String property = order.getProperty();

                if (property.equals("createdAt")) {
                    return new OrderSpecifier<>(direction, problem.createdAt);
                }
                return problem.id.desc();
            }).toArray(OrderSpecifier[]::new);

        } else {
            return List.of(problem.id.desc()).toArray(new OrderSpecifier[0]);
        }
    }
}