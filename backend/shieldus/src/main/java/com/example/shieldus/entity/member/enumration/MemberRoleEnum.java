package com.example.shieldus.entity.member.enumration;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

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

    MemberRoleEnum(Set<Permission> permissions) {
        this.permissions = permissions;
    }


    public List<GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = this.getPermissions().stream()
                .map(permission -> new SimpleGrantedAuthority(permission.name()))
                .collect(Collectors.toList());
        // Optionally, if you also want to add the role itself as a GrantedAuthority (e.g., for hasRole('ADMIN'))
        // Spring Security often prefixes roles with "ROLE_".
        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return authorities;
    }

}
