package com.example.shieldus.service.member;

import com.example.shieldus.controller.dto.MyPageResponse;
import com.example.shieldus.entity.member.Member;
import com.example.shieldus.entity.member.MemberSubmitProblem;
import com.example.shieldus.repository.member.MemberRepository;
import com.example.shieldus.repository.member.MemberSubmitProblemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final MemberSubmitProblemRepository submitProblemRepository;

    // 마이페이지 정보 조회
    public MyPageResponse getMyPageInfo(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid member ID"));

    // 2. 정답 기록 조회 (pass=true인 문제만)
        List<MemberSubmitProblem> solvedProblems = submitProblemRepository
                .findByMemberIdAndPassTrue(memberId); // 정답만 필터링

    // 3. DTO 변환
        return MyPageResponse.builder()
                .name(member.getName())
                .email(member.getEmail())
                .solvedProblems(
                        solvedProblems.stream()
                                .map(sub -> MyPageResponse.SolvedProblem.builder()
                                        .problemTitle(sub.getProblem().getTitle())
                                        .completeDate(sub.getCompleteDate())
                                        .build())
                                .collect(Collectors.toList())
                )
                .build();
    }
}
