package com.example.shieldus.service.member;

import com.example.shieldus.controller.dto.*;
import com.example.shieldus.controller.dto.MemberUpdateRequestDto;
import com.example.shieldus.controller.dto.member.MemberTempCodeResponseDto;
import com.example.shieldus.entity.member.Member;
import com.example.shieldus.entity.member.MemberSubmitProblem;
import com.example.shieldus.entity.member.MemberTempCode;
import com.example.shieldus.exception.CustomException;
import com.example.shieldus.exception.ErrorCode;
import com.example.shieldus.repository.member.MemberRepository;
import com.example.shieldus.repository.member.MemberSubmitProblemRepository;
import com.example.shieldus.repository.member.MemberTempCodeRepository;
import com.example.shieldus.repository.problem.ProblemRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    private final MemberTempCodeRepository tempCodeRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProblemRepository problemRepository;

    /*
    * 마이페이지 정보 가져오기
    * */
    public MyPageResponseDto getMyPageInfo(Long memberId, int page,int size) {
        try {
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

            Pageable pageable = PageRequest.of(page, size, Sort.by("updatedAt").descending());
            Page<MemberSubmitProblem> submissionPage = submitProblemRepository.findByMemberId(memberId, pageable);

            Page<SubmissionDto> dtoPage = submissionPage.map(SubmissionDto::from);

            return MyPageResponseDto.builder()
                    .name(member.getName())
                    .email(member.getEmail())
                    .submissions(dtoPage)
                    .build();
        } catch (DataAccessException e) {
            log.error("Database error in getMyPageInfo", e);
            throw new CustomException(ErrorCode.DATABASE_ERROR, e);
        } catch (Exception e) {
            log.error("Unexpected error in getMyPageInfo", e);
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR, e);
        }
    }
    /*
    * 사용자 수 카운트
    * */
    public StatisticsResponseDto.MemberCount getMemberCount() {
        return memberRepository.getMemberCount();

    }
    /*
     * 일별 사용자 수 카운트
     * */
    public List<StatisticsResponseDto.MemberCount> getDailyMemberCount(){
        return memberRepository.getDailyMemberCounts(7);
    }

    /*
    * MemberSubmitProblem 정보 가져오기
    * */
    public Page<SubmissionDto> getSubmissions(Long memberId, Pageable pageable) {
        return problemRepository.getSubmissions(memberId, pageable);
    }

    /*
     * 사용자 목록조회
     * */
    public Page<MemberResponseDto> getMembers(String searchName, String searchValue, Pageable pageable) {
        return memberRepository.getMembers(searchName, searchValue, pageable);
    }

    /*
    * 사용자 정보조회
    * */
    public MemberResponseDto getMember(Long id) {
        return MemberResponseDto.fromEntity(
                memberRepository.findById(id).orElseThrow(()->new CustomException(ErrorCode.USER_NOT_FOUND)));
    }

    /*
    * 사용자 등록
    * */
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

    /*
     * 사용자 업데이트
     * */
    @Transactional
    public Boolean updateMember(Long id, MemberRequestDto memberRequestDto) {
        Member member = memberRepository.findByIdAndIsDeletedIsFalse(id).orElseThrow(()->new CustomException(ErrorCode.USER_NOT_FOUND));
        member.setEmail(memberRequestDto.getEmail());
        if(memberRequestDto.getPassword() != null && !memberRequestDto.getPassword().isEmpty()) {
            member.setPassword(passwordEncoder.encode(memberRequestDto.getPassword()));
        }
        member.setName(memberRequestDto.getName());
        member.setRole(memberRequestDto.getRole());
        member.setPhone(memberRequestDto.getPhone());

        return true;

    }
    /*
    * 사용자 삭제
    * */
    @Transactional
    public void deleteMember(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        try {
            member.delete();
        } catch (DataAccessException e) {
            throw new CustomException(ErrorCode.DATABASE_ERROR, e);
        }
    }



    /*
    * Solved Problem 하나 가져오기
    * */
    public ProblemResponseDto.SolvedProblem getSolvedProblem(Long memberId, Long submitProblemId) {
        MemberTempCode memberSubmitProblem =  tempCodeRepository.findOneByMemberIdAndMemberSubmitProblemId(memberId, submitProblemId)
                .orElseThrow(() -> new CustomException(ErrorCode.PROBLEM_NOT_FOUND));
        return new ProblemResponseDto.SolvedProblem(memberSubmitProblem);

    }

    /*
     * Member Temp Code 가져오기
     * */
    public MemberTempCodeResponseDto getMemberTempCode(Long memberId, Long problemId){

        Optional<MemberSubmitProblem> submitProblem = submitProblemRepository.findByMemberIdAndProblem_Id(memberId,problemId);

        return submitProblem
                .flatMap(submit ->
                        tempCodeRepository.findTopByMemberSubmitProblemIdOrderBySubmitDateDesc(submit.getId()))
                .map(MemberTempCodeResponseDto::fromEntity)
                .orElse(null);
    }

    public List<MemberTempCodeDto> getTempCodesForSubmission(Long submitProblemId, Long memberId) {
        MemberSubmitProblem submitProblem = submitProblemRepository.findById(submitProblemId)
                .orElseThrow(() -> new CustomException(ErrorCode.PROBLEM_CODE_NOT_FOUND));

        if (!submitProblem.getMember().getId().equals(memberId)) {
            throw new CustomException(ErrorCode.AUTHENTICATION_FAILED);
        }

        List<MemberTempCode> tempCodes = submitProblem.getMemberTempCodes();

        return tempCodes.stream()
                .map(MemberTempCodeDto::from)
                .toList();
    }



}
