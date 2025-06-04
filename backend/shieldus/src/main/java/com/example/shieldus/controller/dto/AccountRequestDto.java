package com.example.shieldus.controller.dto;

import com.example.shieldus.entity.member.Member;
import com.example.shieldus.entity.member.enumration.MemberRoleEnum;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.crypto.password.PasswordEncoder;

public class AccountRequestDto {

    /*
    * Register
    * email : string
    * password : string
    * name : string
    * phone: string
    * */
    @Getter
    public static class Register {

        @NotBlank(message = "이메일은 필수입니다.")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        private String email;

        @NotBlank(message = "비밀번호는 필수입니다.")
        @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하여야 합니다.")
        private String password;

        @NotBlank(message = "이름은 필수입니다.")
        private String name;

        // TODO : pattern 모아놓기.
        @NotBlank(message = "전화번호는 필수입니다.")
        @Pattern(regexp = "^\\d{10,11}$", message = "전화번호는 숫자만 입력하며 10~11자리여야 합니다.")
        private String phone;

        public Member toMember(PasswordEncoder passwordEncoder) {
            return Member.builder()
                    .email(email)
                    .password(passwordEncoder.encode(password))
                    .name(name)
                    .phone(phone)
                    .memberRank(0)
                    .role(MemberRoleEnum.USER)
                    .isDeleted(false)
                    .build();
        }

    }
}


