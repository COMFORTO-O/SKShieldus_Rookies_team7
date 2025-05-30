package com.example.shieldus.service.member;

import com.example.shieldus.controller.dto.AccountRequestDto;
import com.example.shieldus.controller.dto.MyPageResponseDto;
import com.example.shieldus.entity.member.Member;
import com.example.shieldus.entity.member.MemberSubmitProblem;
import com.example.shieldus.entity.member.enumration.MemberRoleEnum;
import com.example.shieldus.exception.CustomException;
import com.example.shieldus.exception.ErrorCode;
import com.example.shieldus.repository.member.MemberRepository;
import com.example.shieldus.repository.member.MemberSubmitProblemRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final MemberSubmitProblemRepository submitProblemRepository;
    private final PasswordEncoder passwordEncoder;

    public MyPageResponseDto getMyPageInfo(Long memberId) {
        try {
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

            List<MemberSubmitProblem> solvedProblems = submitProblemRepository
                    .findByMemberIdAndPassTrue(memberId);

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
            log.error("Database error in getMyPageInfo", e);
            throw new CustomException(ErrorCode.DATABASE_ERROR, e);
        } catch (Exception e) {
            log.error("Unexpected error in getMyPageInfo", e);
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR, e);
        }
    }

    @Transactional
    public void register(AccountRequestDto.Register dto) {
        try {
            if (memberRepository.existsByEmail(dto.getEmail())) {
                throw new CustomException(ErrorCode.DUPLICATE_RESOURCE,"이미 존재하는 이메일입니다.");
            }
            Member member = dto.toMember(passwordEncoder);
            memberRepository.save(member);
        } catch (DataAccessException e) {
            log.error("Database error in register", e);
            throw new CustomException(ErrorCode.DATABASE_ERROR, e);
        } catch (IllegalArgumentException e) {
            throw new CustomException(ErrorCode.INVALID_REQUEST, e);
        } catch (Exception e) {
            log.error("Unexpected error in register", e);
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR, e);
        }
    }
}
