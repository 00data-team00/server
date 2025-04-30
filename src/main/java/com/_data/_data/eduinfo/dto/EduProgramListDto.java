package com._data._data.eduinfo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class EduProgramListDto {
    private List<EduProgramSimpleDto> eduPrograms;
}
