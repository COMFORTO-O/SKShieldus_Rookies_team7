package com.example.shieldus.repository.member;

import com.example.shieldus.controller.dto.ProblemResponseDto;
import com.example.shieldus.entity.member.MemberSubmitProblem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
;
import java.util.List;

public interface MemberSubmitProblemRepositoryCustom{
    Page<ProblemResponseDto> getMemberSubmitProblems(Long memberId, Pageable pageable);

}
