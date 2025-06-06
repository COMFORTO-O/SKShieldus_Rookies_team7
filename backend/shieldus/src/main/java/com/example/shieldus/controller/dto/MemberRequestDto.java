package com.example.shieldus.controller.dto;

import com.example.shieldus.entity.member.enumration.MemberRoleEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberRequestDto {
    private Long id;

    @NotBlank(message = "이름을 입력해주세요")
    private String name;

    @NotBlank(message = "이메일을 입력해주세요")
    private String email;


    @NotBlank(message = "패스워드를 입력해주세요")
    private String password;

    @NotBlank(message = "권한을 입력해주세요")
    private MemberRoleEnum role;

    @NotNull(message = "전화번호를 입력해주세요")
    private String phone;




}