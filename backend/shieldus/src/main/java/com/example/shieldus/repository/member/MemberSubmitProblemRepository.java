package com.example.shieldus.repository.member;

import com.example.shieldus.entity.member.MemberSubmitProblem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MemberSubmitProblemRepository extends JpaRepository<MemberSubmitProblem, Long> {
    // 정답 기록만 조회
    List<MemberSubmitProblem> findByMemberIdAndPassTrue(Long memberId);
}