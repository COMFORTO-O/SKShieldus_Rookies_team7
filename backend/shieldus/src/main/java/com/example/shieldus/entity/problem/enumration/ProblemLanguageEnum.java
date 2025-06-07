package com.example.shieldus.entity.problem.enumration;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public enum ProblemLanguageEnum {


    JAVA("Java",62),
    PYTHON("Python",71),
    //JAVASCRIPT("JavaScript"),
    C("C",50);

    private final String displayName;
    private final Integer judgeNumber;


    ProblemLanguageEnum(String displayName, Integer judgeNumber ) {
        this.displayName = displayName;
        this.judgeNumber = judgeNumber;
    }


    public static List<ProblemLanguageEnum> getAllLanguages() {
        return Arrays.asList(ProblemLanguageEnum.values());
    }
}
