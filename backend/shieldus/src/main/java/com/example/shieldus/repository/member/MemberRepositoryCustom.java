package com.example.shieldus.repository.member;

import com.example.shieldus.controller.dto.MemberResponseDto;
import com.example.shieldus.entity.member.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface MemberRepositoryCustom{

    Page<MemberResponseDto> getMembers(String searchName, String searchValue, Pageable pageable);

}
