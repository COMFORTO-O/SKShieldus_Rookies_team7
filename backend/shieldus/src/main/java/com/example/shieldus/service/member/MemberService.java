package com.example.shieldus.service.member;

import com.example.shieldus.controller.dto.AccountRequestDto;
import com.example.shieldus.controller.dto.MyPageResponseDto;
import com.example.shieldus.entity.member.Member;
import com.example.shieldus.entity.member.MemberSubmitProblem;
import com.example.shieldus.entity.member.enumration.MemberRoleEnum;
import com.example.shieldus.repository.member.MemberRepository;
import com.example.shieldus.repository.member.MemberSubmitProblemRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final MemberSubmitProblemRepository submitProblemRepository;
    private final PasswordEncoder passwordEncoder;

    // 마이페이지 정보 조회
    public MyPageResponseDto getMyPageInfo(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid member ID"));

    // 2. 정답 기록 조회 (pass=true인 문제만)
        List<MemberSubmitProblem> solvedProblems = submitProblemRepository
                .findByMemberIdAndPassTrue(memberId); // 정답만 필터링

    // 3. DTO 변환
        return MyPageResponseDto.builder()
                .name(member.getName())
                .email(member.getEmail())
                .solvedProblems(
                        solvedProblems.stream()
                                .map(sub -> MyPageResponseDto.SolvedProblem.builder()
                                        .problemTitle(sub.getProblem().getTitle())
                                        .completeDate(sub.getCompleteDate())
                                        .build())
                                .collect(Collectors.toList())
                )
                .build();
    }





    // 회원가입
    @Transactional
    public void register(AccountRequestDto.Register dto) {

        if(!memberRepository.existsByEmail(dto.getEmail())) {
            // TODO : exception 추가
            throw new IllegalArgumentException("Invalid email");
        }
        // TODO: 검사 로직 필요
        memberRepository.save(dto.toMember(passwordEncoder));
    }
}
