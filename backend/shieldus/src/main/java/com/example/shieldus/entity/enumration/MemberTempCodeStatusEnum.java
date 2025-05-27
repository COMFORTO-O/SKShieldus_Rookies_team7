package com.example.shieldus.entity.enumration;

import lombok.Getter;


@Getter
public enum MemberTempCodeStatusEnum {

    TEST("Test"),
    CORRECT("정답"),
    INCORRECT("");

    private final String displayName;

    MemberTempCodeStatusEnum(String displayName) {
        this.displayName = displayName;
    }

}