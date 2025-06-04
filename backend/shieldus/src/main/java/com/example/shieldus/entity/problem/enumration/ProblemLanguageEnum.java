package com.example.shieldus.entity.problem.enumration;

import lombok.Getter;

@Getter
public enum ProblemLanguageEnum {


    JAVA("Java"),
    PYTHON("Python"),
    JAVASCRIPT("JavaScript"),
    C("C");

    private final String displayName;


    ProblemLanguageEnum(String displayName) {
        this.displayName = displayName;
    }


}
