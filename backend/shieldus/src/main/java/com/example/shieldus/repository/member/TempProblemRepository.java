package com.example.shieldus.repository.member;

import com.example.shieldus.entity.member.TempProblem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TempProblemRepository extends JpaRepository<TempProblem, Long> {
    List<TempProblem> findByMemberId(Long memberId);
}