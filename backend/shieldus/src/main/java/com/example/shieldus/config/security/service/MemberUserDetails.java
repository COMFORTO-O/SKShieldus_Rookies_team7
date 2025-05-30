package com.example.shieldus.config.security.service;

import com.example.shieldus.entity.member.Member;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MemberUserDetails implements UserDetails {

    private String email;
    private String password;
    private List<GrantedAuthority> authorities;
    private Long id;
    private String name;
    private String phone;
    private Integer memberRank;



    public MemberUserDetails(Member member) {
        this.email=member.getEmail();
        this.password=member.getPassword();
        this.id = member.getId();
        this.name=member.getName();
        this.phone=member.getPhone();
        this.memberRank=member.getMemberRank();
        // TODO : 05-27 : getRole 권한 넣기
        this.authorities= member.getRole().getAuthorities();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    public Long getMemberId(){
        return this.id;
    }
    public String getMemberName(){
        return this.name;
    }
    public String getMemberPhone(){
        return this.phone;
    }
    public Integer getMemberRank(){
        return this.memberRank;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }

}
