package com._data._data.user.dto;


import com._data._data.user.entity.Nation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NationResponseDto {
    private Long id;
    private String code;
    private String name;
    private String nameKo;

    // Entity -> DTO 변환 메서드
    public static NationResponseDto from(Nation nation) {
        return NationResponseDto.builder()
            .id(nation.getId())
            .code(nation.getCode())
            .name(nation.getName())
            .nameKo(nation.getNameKo())
            .build();
    }
}