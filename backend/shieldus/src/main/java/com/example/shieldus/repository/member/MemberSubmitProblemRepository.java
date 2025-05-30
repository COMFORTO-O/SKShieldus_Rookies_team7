package com.example.shieldus.repository.member;

import com.example.shieldus.entity.member.Member;
import com.example.shieldus.entity.member.MemberSubmitProblem;
import com.example.shieldus.entity.problem.Problem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface MemberSubmitProblemRepository extends JpaRepository<MemberSubmitProblem, Long> {
    // 정답 기록만 조회
    List<MemberSubmitProblem> findByMemberIdAndPassTrue(Long memberId);
    Optional<MemberSubmitProblem> findByMemberAndProblem(Member member, Problem problem);
}