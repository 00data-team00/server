package com._data._data.eduinfo.mapper;


import com._data._data.common.util.translation.MultiLanguageTitleSelector;
import com._data._data.eduinfo.dto.EduProgramSimpleDto;
import com._data._data.eduinfo.entity.EduProgram;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class EduProgramDtoMapper {
    private static final String NO_SEARCH_RESULT_MESSAGE = "검색 결과가 없습니다.";

    private final MultiLanguageTitleSelector titleSelector;

    public EduProgramDtoMapper(MultiLanguageTitleSelector titleSelector) {
        this.titleSelector = titleSelector;
    }

    /**
     * EduProgram 엔티티를 EduProgramSimpleDto로 변환
     *
     * @param program 변환할 엔티티
     * @param language 사용자 언어 코드
     * @return 변환된 DTO
     */
    public EduProgramSimpleDto toSimpleDto(EduProgram program, String language) {
        if (program == null) {
            throw new IllegalArgumentException("변환할 프로그램이 null입니다.");
        }

        String localizedTitle = titleSelector.selectTitle(program, language);
        String applicationLink = resolveApplicationLink(program.getAppLink());
        String thumbnailUrl = resolveThumbnailUrl(program.getThumbnailUrl());
        boolean isFree = determineIfFree(program.getTuitEtc());

        return new EduProgramSimpleDto(
            program.getId(),
            localizedTitle,
            program.getAppQual(),
            program.getTuitEtc(),
            program.getAppEndDate(),
            isFree,
            applicationLink,
            thumbnailUrl
        );
    }

    /**
     * 신청 링크 처리 - null이나 빈 값인 경우 기본 메시지 반환
     */
    private String resolveApplicationLink(String appLink) {
        return Optional.ofNullable(appLink)
            .filter(link -> !link.isBlank())
            .orElse(NO_SEARCH_RESULT_MESSAGE);
    }

    /**
     * 썸네일 URL 처리 - null이나 빈 값인 경우 null 반환
     */
    private String resolveThumbnailUrl(String thumbnailUrl) {
        return Optional.ofNullable(thumbnailUrl)
            .filter(url -> !url.isBlank())
            .orElse(null);
    }

    /**
     * 무료 여부 판단 - 수강료 정보가 없거나 빈 값인 경우 무료로 판단
     */
    private boolean determineIfFree(String tuitionInfo) {
        return tuitionInfo == null || tuitionInfo.isBlank();
    }
}
