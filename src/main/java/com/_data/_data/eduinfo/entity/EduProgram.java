package com._data._data.eduinfo.entity;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 서울외국인포털 교육프로그램 오픈 api
 *
 * */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Getter
@Setter
@Table(name = "edu_program", uniqueConstraints = @UniqueConstraint(columnNames = {"titleNm", "langGb"}))
public class EduProgram {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titleNm;

    private String langGb;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    private String cont;

    private LocalDate appStartDate;

    private LocalTime appStartTime;

    private LocalDate appEndDate;

    private LocalTime appEndTime;

    private boolean appEndYn;

    private LocalDate eduStartDate;

    private LocalTime eduStartTime;

    private LocalDate eduEndDate;

    private LocalTime eduEndTime;

    private String appQual;

    private String appWayEtc;

    private String tuitEtc;

    private Integer pers;

    private LocalDateTime regDt;

    private LocalDateTime updDt;

    @Column(length = 500)
    private String titleEn;   // 영어

    @Column(length = 500)
    private String titleZh;   // 중국어 간체

    @Column(length = 500)
    private String titleJa;   // 일본어

    @Column(length = 500)
    private String titleVi;   // 베트남어

    @Column(length = 500)
    private String titleId;   // 인도네시아어

    @Column(length = 1000)
    private String appLink;

    @Column(length = 1000)
    private String thumbnailUrl;
    public void updateFrom(EduProgram source) {
        this.langGb = source.langGb;
        this.cont = source.cont;
        this.appStartDate = source.appStartDate;
        this.appStartTime = source.appStartTime;
        this.appEndDate = source.appEndDate;
        this.appEndTime = source.appEndTime;
        this.appEndYn = source.appEndYn;
        this.eduStartDate = source.eduStartDate;
        this.eduStartTime =  source.eduStartTime;
        this.eduEndDate = source.eduEndDate;
        this.eduEndTime = source.eduEndTime;
        this.appQual = source.appQual;
        this.appWayEtc = source.appWayEtc;
        this.tuitEtc = source.tuitEtc;
        this.pers = source.pers;
        this.regDt = source.regDt;
        this.updDt = source.updDt;
        this.titleEn = source.titleEn;
        this.titleZh = source.titleZh;
        this.titleJa = source.titleJa;
        this.titleVi = source.titleVi;
        this.titleId = source.titleId;
        this.appLink = source.appLink;
        this.thumbnailUrl = source.thumbnailUrl;  // 썸네일 URL 업데이트 추가
    }
}
