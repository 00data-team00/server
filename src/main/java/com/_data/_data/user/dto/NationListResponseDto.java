package com._data._data.user.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NationListResponseDto {
    private int totalCount;
    private List<NationResponseDto> nations;

    public static NationListResponseDto of(List<NationResponseDto> nations) {
        return NationListResponseDto.builder()
            .totalCount(nations.size())
            .nations(nations)
            .build();
    }
}