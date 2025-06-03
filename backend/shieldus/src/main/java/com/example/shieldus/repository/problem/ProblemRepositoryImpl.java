package com.example.shieldus.repository.problem;

import com.example.shieldus.controller.dto.ProblemResponseDto;
import com.example.shieldus.entity.member.QMember;
import com.example.shieldus.entity.member.QMemberSubmitProblem;
import com.example.shieldus.entity.problem.QProblem;
import com.example.shieldus.entity.problem.enumration.ProblemCategoryEnum;
import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

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
                        problem.category.description,
                        memberSubmitProblem.pass,
                        memberSubmitProblem.completedAt
                ))
                .from(problem)
                .leftJoin(problem.category)
                .leftJoin(memberSubmitProblem)
                     .on(memberSubmitProblem.member.id.eq(memberId)
                    .and(memberSubmitProblem.problem.id.eq(problem.id)))
                .where(
                        eqCategory(category),
                        eqLevel(level),
                        containsTitle(title),
                        eqStatus(memberId, solved)
                )
                .orderBy(problemOrderBy(pageable))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory.select(problem.count())
                .from(problem)
                .leftJoin(problem.category)
                .leftJoin(memberSubmitProblem)
                .on(memberSubmitProblem.member.id.eq(memberId)
                        .and(memberSubmitProblem.problem.id.eq(problem.id)))
                .where(
                        eqCategory(category),
                        eqLevel(level),
                        containsTitle(title),
                        eqStatus(memberId, solved)
                )
                .fetchOne();
        if (total == null){
            total = 0L;
        }
        return new PageImpl<>(problemResponses, pageable , 0);
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
        return memberId != null && memberId > 0 && solved != null ? ( solved ? memberSubmitProblem.pass.isTrue() : memberSubmitProblem.pass.isFalse()): null;
    }

    private OrderSpecifier<?>[] problemOrderBy(Pageable pageable) {
        if(pageable.getSort().isSorted()){
            return pageable.getSort().stream().map(order -> {
                Order direction = order.isAscending() ? Order.ASC : Order.DESC;
                String property = order.getProperty();

                if(property.equals("createdAt")){
                    return new OrderSpecifier<>(direction, problem.createdAt);
                }
                return problem.id.desc();
            }).toArray(OrderSpecifier[]::new);

        }
        else {
            return List.of(problem.id.desc()).toArray(new OrderSpecifier[0]);
        }
    }
}