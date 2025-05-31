package com.example.shieldus.repository.member;


import com.example.shieldus.entity.member.MemberSubmitProblem;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.shieldus.entity.member.QMemberSubmitProblem.memberSubmitProblem;

@Repository
@RequiredArgsConstructor
public class MemberSubmitProblemRepositoryCustomImpl implements MemberSubmitProblemRepositoryCustom {


    private final JPAQueryFactory queryFactory;

    /*
    * 푼 문제 제목과, submit 의 컬럼을 가져와 dto 로 제작
    * */
    @Override
    public Page<MemberSubmitProblem> getMemberSubmitProblems(Long memberId, Pageable pageable) {
        queryFactory.select()
                .from(memberSubmitProblem)
                .where(memberSubmitProblem.member.id.eq(memberId).and(
                        memberSubmitProblem.pass.isTrue()
                ))
                .orderBy(memberSubmitProblem.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(memberSubmitProblem.count())
                .from(memberSubmitProblem)
                .where(
                        memberSubmitProblem.member.id.eq(memberId)
                        .and(memberSubmitProblem.pass.isTrue())
                )
                .fetchOne();


        return new PageImpl<>(null, pageable, total != null ? total : 0L);
    }
}
