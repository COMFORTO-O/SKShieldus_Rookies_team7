package com.example.shieldus.repository.member;

import com.example.shieldus.entity.member.MemberTempCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberTempCodeRepository extends JpaRepository<MemberTempCode, Long> {
    // 필요시 사용자 ID나 문제 ID로 필터링하는 메서드도 추가 가능
}
