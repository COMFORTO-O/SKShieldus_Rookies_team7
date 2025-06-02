package com.example.shieldus.repository.member;

import com.example.shieldus.entity.member.Member;
import com.example.shieldus.entity.member.MemberSubmitProblem;
import com.example.shieldus.entity.problem.Problem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberSubmitProblemRepository extends JpaRepository<MemberSubmitProblem, Long> {

    // (기존) 회원이 pass=true로 제출한 문제 목록 조회
    List<MemberSubmitProblem> findByMemberIdAndPassTrue(Long memberId);

    // (추가) memberId와 problemId로 한 건 조회 → 로그인 사용자의 solved 여부 판단
    Optional<MemberSubmitProblem> findByMemberIdAndProblemId(Long memberId, Long problemId);

    // (추가) 특정 문제를 pass=true로 제출한 모든 기록 조회 → 문제 solved list 조회용
    List<MemberSubmitProblem> findByProblemAndPassTrue(Problem problem);

    // (기존) Member 객체와 Problem 객체로 조회
    Optional<MemberSubmitProblem> findByMemberAndProblem(Member member, Problem problem);
}