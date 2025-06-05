package com.example.shieldus.repository.member;


import com.example.shieldus.controller.dto.ProblemResponseDto;
import com.example.shieldus.entity.member.MemberSubmitProblem;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.shieldus.entity.member.QMemberSubmitProblem.memberSubmitProblem;
import static com.example.shieldus.entity.problem.QProblem.problem;

@Repository
@RequiredArgsConstructor
public class MemberSubmitProblemRepositoryCustomImpl implements MemberSubmitProblemRepositoryCustom {


}
