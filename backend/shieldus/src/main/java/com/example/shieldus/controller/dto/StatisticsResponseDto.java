package com.example.shieldus.controller.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class StatisticsResponseDto {

    private MemberCount memberCount;
    private List<MemberCount> dailyMemberCount;

    @Getter
    @Setter
    public static class MemberCount{
        private Long totalCount;
        private Long createCount;
        private Long deleteCount;
        private LocalDate date;


        public MemberCount(Long createCount, Date date) {
            this.createCount = createCount;
            this.date =  LocalDate.parse(date.toString());
        }
        public MemberCount(Integer createCount, LocalDate date) {
            this.createCount = createCount.longValue();
            this.date = date;
        }

        public MemberCount(Long totalCount, Long createCount, Long deleteCount) {
            this.totalCount = totalCount;
            this.createCount = createCount;
            this.deleteCount = deleteCount;
        }



        public MemberCount(Long totalCount, Long createCount, Long deleteCount, LocalDate date) {
            this.totalCount = totalCount;
            this.createCount = createCount;
            this.deleteCount = deleteCount;
            this.date = date;

        }
    }
}
