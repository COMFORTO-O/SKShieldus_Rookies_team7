package com.example.shieldus.entity.member.enumration;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public enum MemberRoleEnum {
    ADMIN(Set.of(
            Permission.PROBLEM_CREATE,
            Permission.PROBLEM_READ,
            Permission.PROBLEM_UPDATE,
            Permission.PROBLEM_DELETE)),
    USER(Set.of(
            Permission.PROBLEM_READ
    ));

    private final Set<Permission> permissions;
    @Getter
    private final List<GrantedAuthority> authorities;

    MemberRoleEnum(Set<Permission> permissions) {
        this.permissions = permissions;
        // 각 Permission을 SimpleGrantedAuthority로 변환하여 authorities 필드 초기화
        List<GrantedAuthority> grantedPermissions = permissions.stream()
                .map(permission -> new SimpleGrantedAuthority(permission.name()))
                .collect(Collectors.toUnmodifiableList());
        List<GrantedAuthority> combinedAuthorities = new ArrayList<>(grantedPermissions);
        combinedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        this.authorities = combinedAuthorities;
    }


}
