package com.example.shieldus.repository.member;

import com.example.shieldus.entity.member.MemberTempCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberTempCodeRepository extends JpaRepository<MemberTempCode, Long> {
    // 필요시 사용자 ID나 문제 ID로 필터링하는 메서드도 추가 가능



    @Query("SELECT m FROM MemberTempCode m where " +
            "m.memberSubmitProblem.id = :problemId and " +
            "m.memberSubmitProblem.member.id = :memberId and " +
            "m.memberSubmitProblem.pass is true " +
            "ORDER BY m.id DESC " +
            "LIMIT 1")
    Optional<MemberTempCode> findOneByMemberIdAndMemberSubmitProblemId(@Param("memberId") Long memberId, @Param("problemId") Long problemId);

    @Query("""
        SELECT m FROM MemberTempCode m
        WHERE m.memberSubmitProblem.problem.id = :problemId
          AND m.memberSubmitProblem.member.id = :memberId
        ORDER BY m.submitDate DESC
        """)
    Optional<MemberTempCode> findTopByProblemIdAndMemberIdOrderBySubmitDateDesc(
            @Param("problemId") Long problemId,
            @Param("memberId") Long memberId
    );
    Optional<MemberTempCode> findTopByMemberSubmitProblemIdOrderBySubmitDateDesc(Long submitProblemId);
}
