package com.example.shieldus.controller;

import com.example.shieldus.config.security.service.MemberUserDetails;
import com.example.shieldus.controller.dto.*;
import com.example.shieldus.controller.dto.member.MemberTempCodeResponseDto;
import com.example.shieldus.entity.member.MemberSubmitProblem;
import com.example.shieldus.entity.member.enumration.MemberRoleEnum;
import com.example.shieldus.exception.CustomException;
import com.example.shieldus.exception.ErrorCode;
import com.example.shieldus.service.member.MemberService;
import com.example.shieldus.service.problem.ProblemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/member") // 공통 URL prefix 설정
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final ProblemService problemService;



//    @GetMapping("/info")
//    public ResponseDto<MyInfoResponseDto> getUserInfo(@AuthenticationPrincipal MemberUserDetails userDetails) {
//        MyInfoResponseDto myInfoResponseDto = memberService.getMyInfo(userDetails.getMemberId());
//        //기존 info = {name,email,solvedproblem} => 변경된 info = {name,email,memberRank}
//        return ResponseDto.success(myInfoResponseDto);
//    }


//    @GetMapping("/problem/solved/detail/{submitProblemId}") // 푼 문제 상세정보 / id = member_submit_problem_id;
//    public ResponseDto<ProblemResponseDto.SolvedProblem> getSolvedProblemDetail(
//            @PathVariable Long submitProblemId,
//            @AuthenticationPrincipal MemberUserDetails userDetails) {
//        ProblemResponseDto.SolvedProblem solvedProblem = memberService.getSolvedProblem(submitProblemId, userDetails.getMemberId());
//        return ResponseDto.success(solvedProblem);
//    }

    @GetMapping("/problem/submitcodes/{submitProblemId}") // 해당 문제에 대한 제출 내역(코드) 보기
    public ResponseDto<List<MemberTempCodeDto>> getSolvedProblemDetail(
            @PathVariable Long submitProblemId,
            @AuthenticationPrincipal MemberUserDetails userDetails) {
        List<MemberTempCodeDto> codes = memberService.getTempCodesForSubmission(
                submitProblemId, userDetails.getMemberId());
        return ResponseDto.success(codes);
    }


    //문제 입장 시 임시 저장 코드 받아오기

    @DeleteMapping("/delete")// 사용자 삭제 ( soft ), 진짜 삭제가 아닌 컬럼 붙여서 삭제
    public ResponseDto<String> deleteMember(@AuthenticationPrincipal MemberUserDetails userDetails) {
        memberService.deleteMember(userDetails.getMemberId());
        return ResponseDto.success("ok");
    }

    @GetMapping("/problem/temp/{problemId}")
    public ResponseDto<MemberTempCodeResponseDto> getProblemTemp(
            @PathVariable Long problemId,
            @AuthenticationPrincipal MemberUserDetails userDetails) {

        MemberTempCodeResponseDto tempCode = memberService.getMemberTempCode(userDetails.getMemberId(), problemId);

        // 필요하다면 메시지도 따로 설정 가능
        if (tempCode == null) {
            return ResponseDto.success(MemberTempCodeResponseDto
                    .builder()
                    .code("// 코드를 입력하세요.")
                    .build());
        }else {
            System.out.println(tempCode.getCode()+"asdfasdfsadf");
        }

        return ResponseDto.success(tempCode);
    }


    // 회원 목록 조회
    @PreAuthorize("hasAnyAuthority('PROBLEM_READ', 'ADMIN_READ')")
    @GetMapping("/list") // 푼 문제 상세정보 / id = member_submit_problem_id;
    public ResponseDto<Page<MemberResponseDto>> getSolvedProblemDetail(
            @RequestParam(required = false) String searchName,
            @RequestParam(required = false) String searchValue,
            @PageableDefault(size = 100, sort = "id,desc") Pageable pageable,
            @AuthenticationPrincipal MemberUserDetails userDetails) {
        Page<MemberResponseDto> solvedProblem = memberService.getMembers(searchName, searchValue, pageable);
        return ResponseDto.success(solvedProblem);
    }

    // 회원 목록 조회
    @PreAuthorize("hasAnyAuthority('MEMBER_UPDATE', 'ADMIN_UPDATE')")
    @PostMapping("/update") // 푼 문제 상세정보 / id = member_submit_problem_id;
    public ResponseDto<Boolean> getSolvedProblemDetail(
            @RequestBody MemberRequestDto memberRequestDto,
            @AuthenticationPrincipal MemberUserDetails userDetails) {
        try{
            Long memberId = getIdCheck(memberRequestDto.getId(), userDetails);
            memberService.updateMember(memberId, memberRequestDto);
            return ResponseDto.success(true);
        }catch(CustomException e){
            return ResponseDto.error(e.getErrorCode());
        }catch (Exception e){
            return ResponseDto.error(ErrorCode.INTERNAL_SERVER_ERROR);
        }

    }


    // 사용자 조회.
    @PreAuthorize("hasAnyAuthority('MEMBER_READ', 'ADMIN_READ')")
    @GetMapping("/info")
    public ResponseDto<MemberResponseDto.Detail> getMemberInfo(
            @RequestParam(required = false) Long id,
            @PageableDefault(size = 10, sort = "id,desc") Pageable pageable,
            @AuthenticationPrincipal MemberUserDetails userDetails
    ) {
        try{
            Long memberId = getIdCheck(id, userDetails);
            // ResponseDto 불러와 제작
            MemberResponseDto myPageData = memberService.getMember(memberId);
            float ranking = memberService.calculateRanking(memberId);
            myPageData.setRanking(ranking);
            Page<SubmissionDto> submissionDtoPage = memberService.getSubmissions(memberId, pageable);
            return ResponseDto.success(new MemberResponseDto.Detail(myPageData, submissionDtoPage));
        }catch(CustomException e){
            return ResponseDto.error(e.getErrorCode());
        }catch (Exception e){
            return ResponseDto.error(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    // 사용자 삭제 요청
    @PreAuthorize("hasAnyAuthority('ADMIN_DELETE')")
    @DeleteMapping("/delete/{id}")// 사용자 삭제 ( soft ), 진짜 삭제가 아닌 컬럼 붙여서 삭제
    public ResponseDto<String> deleteMember(@PathVariable Long id, @AuthenticationPrincipal MemberUserDetails userDetails) {
        memberService.deleteMember(id);
        return ResponseDto.success("ok");
    }


    private Long getIdCheck(Long id,MemberUserDetails userDetails) {
        Long memberId = null;
        if(userDetails.getRole().equals(MemberRoleEnum.USER)){
            if(id == null){
                memberId = userDetails.getMemberId();
            }else{
                throw new CustomException(ErrorCode.AUTHENTICATION_FAILED);
            }

        }else if (userDetails.getRole().equals(MemberRoleEnum.ADMIN)){
            memberId = id == null || id == 0 ? userDetails.getMemberId() : id;
        }
        return memberId;
    }
}
