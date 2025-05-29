package com.example.shieldus.service.member;

import com.example.shieldus.controller.dto.AccountRequestDto;
import com.example.shieldus.controller.dto.MyPageResponseDto;
import com.example.shieldus.entity.member.Member;
import com.example.shieldus.entity.member.MemberSubmitProblem;
import com.example.shieldus.exception.CustomException;
import com.example.shieldus.exception.ErrorCode;
import com.example.shieldus.repository.member.MemberRepository;
import com.example.shieldus.repository.member.MemberSubmitProblemRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final MemberSubmitProblemRepository submitProblemRepository;
    private final PasswordEncoder passwordEncoder;

    // 마이페이지 정보 조회 (예외 처리 추가)
    public MyPageResponseDto getMyPageInfo(Long memberId) {
        try {
            // 1. 회원 존재 여부 확인 (CustomException 사용)
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

            // 2. 정답 기록 조회 (DataAccessException 자동 처리)
            List<MemberSubmitProblem> solvedProblems = submitProblemRepository
                    .findByMemberIdAndPassTrue(memberId);

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

        } catch (DataAccessException e) {
            // DB 접근 오류 발생 시
            throw new CustomException(ErrorCode.DATABASE_ERROR, e);
        }
    }

    // 회원가입 (예외 처리 추가)
    @Transactional
    public void register(AccountRequestDto.Register dto) {
        try {
            // 1. 이메일 중복 체크
            if (memberRepository.existsByEmail(dto.getEmail())) {
                throw new CustomException(ErrorCode.DUPLICATE_RESOURCE);
            }

            // 2. 회원 저장
            memberRepository.save(dto.toMember(passwordEncoder));

        } catch (DataAccessException e) {
            // DB 오류 (저장 실패 등)
            throw new CustomException(ErrorCode.DATABASE_ERROR, e);
        } catch (IllegalArgumentException e) {
            // 유효하지 않은 입력값 (예: 비밀번호 규칙 위반)
            throw new CustomException(ErrorCode.INVALID_REQUEST, e);
        }
    }
}