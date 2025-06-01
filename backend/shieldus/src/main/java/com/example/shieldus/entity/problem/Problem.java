package com.example.shieldus.entity.problem;


import com.example.shieldus.controller.dto.ProblemRequestDto;
import com.example.shieldus.entity.member.Member;
import com.example.shieldus.entity.problem.enumration.ProblemCategoryEnum;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


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
    private LocalDateTime deletedAt = null;

    @OneToMany(mappedBy = "problem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProblemTestCase> testCases = new ArrayList<>();




    public void delete() {
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
    }


    public void update(ProblemRequestDto.Update dto){
        this.title = dto.getTitle();
        this.detail = dto.getDetail();
        this.category = dto.getCategory();
        this.level = dto.getLevel();
        this.isDeleted = false;

    }
}


