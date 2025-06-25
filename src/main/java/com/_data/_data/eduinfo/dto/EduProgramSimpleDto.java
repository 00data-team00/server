package com._data._data.eduinfo.dto;

import java.time.LocalDate;

public record EduProgramSimpleDto(
    Long id,
    String titleNm,
    String appQual,
    String tuitEtc,
    LocalDate appEndDate,
    boolean isFree,
    String appLink
) {}
