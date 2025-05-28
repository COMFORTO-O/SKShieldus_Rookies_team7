package com.example.shieldus.entity.problem.enumration;


import lombok.Getter;

@Getter
public enum ProblemCategoryEnum {

    JAVA("Java"),
    PYTHON("Python"),
    JAVASCRIPT("JavaScript"),
    C("C"),
    ALGORITHM("Algorithm");
    private final String displayName;

    ProblemCategoryEnum(String displayName) {
        this.displayName = displayName;
    }

}