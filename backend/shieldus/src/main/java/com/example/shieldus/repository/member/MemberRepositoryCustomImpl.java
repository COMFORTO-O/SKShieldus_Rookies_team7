package com.example.shieldus.repository.member;

import com.example.shieldus.controller.dto.MemberResponseDto;
import com.example.shieldus.controller.dto.StatisticsResponseDto;
import com.example.shieldus.entity.member.QMember;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.DateTemplate;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.shieldus.entity.member.QMember.member;


@Repository
@RequiredArgsConstructor
public class MemberRepositoryCustomImpl implements MemberRepositoryCustom {

    private final JPAQueryFactory queryFactory;


    public Page<MemberResponseDto> getMembers(String searchName, String searchValue, Pageable pageable) {
        BooleanExpression search = null;
        if (searchName != null && !searchName.isEmpty()) {
            if (searchName.equals("name"))
                search = QMember.member.name.containsIgnoreCase(searchValue);
            if (searchName.equals("email"))
                search = QMember.member.email.containsIgnoreCase(searchValue);
            if (searchName.equals("phone"))
                search = QMember.member.phone.containsIgnoreCase(searchValue);
//            if (searchName.equals("role"))
//                search = QMember.member.role.eq(searchValue);

        }

        List<MemberResponseDto> memberDtoList = queryFactory.select(Projections.constructor(
                        MemberResponseDto.class,
                        member.id,
                        member.email,
                        member.name,
                        member.phone,
                        member.role,
                        member.isDeleted
                ))
                .from(member)
                .where(search)
                .orderBy(problemOrderBy(pageable))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();


        Long total = queryFactory.select(member.count())
                .from(member)
                .where(search)
                .fetchOne(); // total count 쿼리에서는 limit과 offset이 필요 없습니다.
        if (total == null) {
            total = 0L;
        }

        return new PageImpl<>(memberDtoList, pageable, total);
    }

    private OrderSpecifier<?>[] problemOrderBy(org.springframework.data.domain.Pageable pageable) {
        if (pageable.getSort().isSorted()) {
            return pageable.getSort().stream().map(order -> {
                Order direction = order.isAscending() ? Order.ASC : Order.DESC;
                String property = order.getProperty();

                if (property.equals("createdAt")) {
                    return new OrderSpecifier<>(direction, member.createdAt); // createdAt으로 변경
                }
                return member.id.desc();
            }).toArray(OrderSpecifier[]::new);

        } else {
            return List.of(member.id.desc()).toArray(new OrderSpecifier[0]);
        }
    }


    /*
     * 통계를 위한 회원 수 카운트
     * */
    public StatisticsResponseDto.MemberCount getMemberCount() {

        // 1. 전체 회원 수 (is_deleted와 상관없이)
        Long totalCount = queryFactory.select(member.count())
                .from(member)
                .fetchOne();

        if (totalCount == null) {
            totalCount = 0L;
        }

        // 생성된 회원수
        Long createCountToday = queryFactory.select(member.count())
                .from(member)
                .where(member.isDeleted.isFalse())
                .fetchOne();

        if (createCountToday == null) {
            createCountToday = 0L;
        }

        // 삭제된 회원 수
        Long deleteCountToday = queryFactory.select(member.count())
                .from(member)
                .where(member.isDeleted.isTrue())
                .fetchOne();

        if (deleteCountToday == null) {
            deleteCountToday = 0L;
        }

        // StatisticsResponseDto.MemberCount DTO 반환
        return new StatisticsResponseDto.MemberCount(
                totalCount,
                createCountToday,
                deleteCountToday
        );
    }

    public List<StatisticsResponseDto.MemberCount> getDailyMemberCounts(int days) {
        LocalDateTime endDate = LocalDateTime.now().with(LocalTime.MAX);
        LocalDateTime startDate = LocalDate.now().minusDays(days - 1).atStartOfDay();

        DateTemplate<LocalDate> formattedDate = Expressions.dateTemplate(
                LocalDate.class,
                "DATE_FORMAT({0}, {1})",
                member.createdAt,
                "%Y-%m-%d"
        );

        Expression<Long> createCountPath = member.id.count().as("createCount");
        Expression<Date> datePath = Expressions.dateTemplate(
                Date.class,
                "STR_TO_DATE({0}, {1})",
                formattedDate,
                "%Y-%m-%d"
        ).as("date");

        List<Tuple> dailyCountsTuples = queryFactory.select(
                        createCountPath,
                        datePath
                )
                .from(member)
                .where(member.createdAt.between(startDate, endDate))
                .groupBy(formattedDate)
                .orderBy(formattedDate.asc())
                .fetch();

        List<StatisticsResponseDto.MemberCount> dailyMemberCounts = dailyCountsTuples.stream()
                .map(tuple -> new StatisticsResponseDto.MemberCount(
                        tuple.get(createCountPath),
                        tuple.get(datePath)
                ))
                .collect(Collectors.toList());

        return dailyMemberCounts;
    }
}