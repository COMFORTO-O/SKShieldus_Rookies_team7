package com.example.shieldus.entity.propblem;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "problem")
public class Problem extends BaseEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long problemId;

    // TODO : 추후 UserEntity 변경
    private Long userId;

    private String title;
    private String detail;
    private String category;

    private Integer level;









}
