package com.example.shieldus.repository.member;

import com.example.shieldus.entity.member.Member;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

    Optional<Member> findByEmail(String email);


    boolean existsByEmail(String email);

    Optional<Member> findByIdAndIsDeletedIsFalse(Long memberId);

}
