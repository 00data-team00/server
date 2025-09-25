package com._data._data.common.util.translation;

import com._data._data.eduinfo.entity.EduProgram;
import org.springframework.stereotype.Component;

/**
 * 다국어 제목 선택을 위한 전략 패턴 구현
 */

@Component
 public class MultiLanguageTitleSelector {
    /**
     * 사용자 언어에 따라 적절한 제목을 선택
     *
     * @param program 교육 프로그램 엔티티
     * @param language 사용자 언어 코드
     * @return 선택된 언어의 제목
     */
    public String selectTitle(EduProgram program, String language) {
        if (program == null || language == null) {
            return program != null ? program.getTitleNm() : null;
        }

        return switch (language.toLowerCase()) {
            case "en", "en-us", "en-gb" -> getValueOrDefault(program.getTitleEn(), program.getTitleNm());
            case "zh", "zh-cn", "zh-tw" -> getValueOrDefault(program.getTitleZh(), program.getTitleNm());
            case "ja", "jp" -> getValueOrDefault(program.getTitleJa(), program.getTitleNm());
            case "vi", "vn" -> getValueOrDefault(program.getTitleVi(), program.getTitleNm());
            case "id" -> getValueOrDefault(program.getTitleId(), program.getTitleNm());
            default -> program.getTitleNm();
        };
    }

    /**
     * 번역된 값이 없으면 기본값을 반환
     */
    private String getValueOrDefault(String translatedValue, String defaultValue) {
        return (translatedValue != null && !translatedValue.isBlank())
            ? translatedValue
            : defaultValue;
    }
}
