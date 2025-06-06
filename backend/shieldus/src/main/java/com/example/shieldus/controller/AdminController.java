package com.example.shieldus.controller;


import com.example.shieldus.controller.dto.ResponseDto;
import com.example.shieldus.controller.dto.StatisticsResponseDto;
import com.example.shieldus.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@PreAuthorize("hasAnyAuthority('ADMIN_READ')")
@RequestMapping("/api/admin/statistics")
@RequiredArgsConstructor
public class AdminController {

    private final MemberService memberService;

    /*
    * 전체 통계를 보기 위한 컨트롤러
    * 일별 사용자 수, 전체 사용자 수, 삭제된 사용자 수, 활성 문제수
    *
    * */
    @GetMapping
    public ResponseDto<StatisticsResponseDto> getStatistics(){
        StatisticsResponseDto.MemberCount memberCount = memberService.getMemberCount();
        List<StatisticsResponseDto.MemberCount> dailyMemberCount = memberService.getDailyMemberCount();


        return ResponseDto.success(new StatisticsResponseDto(memberCount, dailyMemberCount));
    }
}
