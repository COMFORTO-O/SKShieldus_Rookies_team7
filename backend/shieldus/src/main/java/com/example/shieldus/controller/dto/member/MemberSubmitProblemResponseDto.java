package com.example.shieldus.controller.dto.member;

import com.example.shieldus.controller.dto.ProblemResponseDto;
import com.example.shieldus.entity.member.MemberTempCode;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class MemberSubmitProblemResponseDto {


    @Getter
    @Setter
    public static class SolvedProblem {
        private ProblemResponseDto problem;
        private MemberTempCodeResponseDto tempCode;
        private LocalDateTime completeDate;
        private Boolean pass;

        public SolvedProblem(MemberTempCode code){
            this.problem = ProblemResponseDto.fromProblem(code.getMemberSubmitProblem().getProblem());
            this.tempCode = MemberTempCodeResponseDto.fromEntity(code);
            this.completeDate = code.getSubmitDate();
            this.pass = code.getMemberSubmitProblem().getPass();
        }
    }


}
