package com.example.shieldus.repository.member;

import com.example.shieldus.controller.dto.MemberResponseDto;
import com.example.shieldus.entity.member.QMember;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.shieldus.entity.member.QMember.member;


@Repository
@RequiredArgsConstructor
public class MemberRepositoryCustomImpl implements MemberRepositoryCustom {

    private final JPAQueryFactory queryFactory;


    public Page<MemberResponseDto> getMembers(String searchName, String searchValue, Pageable pageable) {
        BooleanExpression search = null;
        if (searchName != null && !searchName.isEmpty()) {
            if (searchName.equals("name"))
                search = QMember.member.name.eq(searchValue);
            if (searchName.equals("email"))
                search = QMember.member.email.eq(searchValue);
            if (searchName.equals("phone"))
                search = QMember.member.phone.eq(searchValue);
//            if (searchName.equals("role"))
//                search = QMember.member.role.eq(searchValue);

        }

        List<MemberResponseDto> memberDtoList = queryFactory.select(Projections.constructor(
                        MemberResponseDto.class,
                        member.id,
                        member.name,
                        member.phone,
                        member.email,
                        member.role
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
                .orderBy(problemOrderBy(pageable))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchOne();
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
                    return new OrderSpecifier<>(direction, member.id);
                }
                return member.id.desc();
            }).toArray(OrderSpecifier[]::new);

        } else {
            return List.of(member.id.desc()).toArray(new OrderSpecifier[0]);
        }
    }
}
