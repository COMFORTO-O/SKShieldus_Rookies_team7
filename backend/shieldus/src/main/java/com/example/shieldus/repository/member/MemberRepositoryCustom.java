package com.example.shieldus.repository.member;

import com.example.shieldus.controller.dto.MemberResponseDto;
import com.example.shieldus.controller.dto.StatisticsResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface MemberRepositoryCustom{

    Page<MemberResponseDto> getMembers(String searchName, String searchValue, Pageable pageable);

    StatisticsResponseDto.MemberCount getMemberCount();
    List<StatisticsResponseDto.MemberCount> getDailyMemberCounts(int days);

}
