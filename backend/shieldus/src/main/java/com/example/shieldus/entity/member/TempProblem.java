package com.example.shieldus.entity.member;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class TempProblem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private String title;
    private String content;
    private LocalDateTime savedAt;

    @Builder
    public TempProblem(Member member, String title, String content) {
        this.member = member;
        this.title = title;
        this.content = content;
        this.savedAt = LocalDateTime.now();
    }
}
