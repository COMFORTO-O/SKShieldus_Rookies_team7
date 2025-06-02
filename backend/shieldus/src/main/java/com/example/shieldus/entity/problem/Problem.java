package com.example.shieldus.entity.problem;


import com.example.shieldus.entity.member.Member;
import com.example.shieldus.entity.problem.enumration.ProblemCategoryEnum;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "problem")
public class Problem extends BaseEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private String title;

    private String detail;

    @Enumerated(EnumType.STRING)
    private ProblemCategoryEnum category;

    private Integer level;

    // 3. 소프트 삭제 관련 필드 추가
    @Column(name = "is_deleted", nullable = false, columnDefinition = "boolean default false")
    private Boolean isDeleted = false;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // delete() 메서드
    public void delete() {
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();


    }

}


