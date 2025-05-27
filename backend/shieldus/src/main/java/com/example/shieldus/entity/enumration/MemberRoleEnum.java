package com.example.shieldus.entity.enumration;

import lombok.Getter;

import java.util.Set;

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

}
