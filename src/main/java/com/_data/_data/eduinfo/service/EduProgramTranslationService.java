package com._data._data.eduinfo.service;

import com._data._data.aichat.service.TranslationService;
import com._data._data.eduinfo.entity.EduProgram;
import java.util.function.Supplier;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EduProgramTranslationService {
    private static final String SOURCE_LANGUAGE = "ko";
    private static final String[] TARGET_LANGUAGES = {"en-US", "zh", "ja", "vi", "id"};

    private final TranslationService translationService;

    public EduProgramTranslationService(TranslationService translationService) {
        this.translationService = translationService;
    }

    /**
     * 기존 프로그램에서 누락된 번역을 채워넣음
     *
     * @param program 번역을 채울 프로그램 엔티티
     * @return 번역이 업데이트되었는지 여부
     */
    public boolean fillMissingTranslations(EduProgram program) {
        String originalTitle = program.getTitleNm();
        if (originalTitle == null || originalTitle.isBlank()) {
            log.warn("원본 제목이 없어 번역 패스. ID: {}", program.getId());
            return false;
        }

        boolean hasUpdates = false;

        hasUpdates |= fillTranslationIfEmpty(
            program::getTitleEn, program::setTitleEn, originalTitle, "en-US");
        hasUpdates |= fillTranslationIfEmpty(
            program::getTitleZh, program::setTitleZh, originalTitle, "zh");
        hasUpdates |= fillTranslationIfEmpty(
            program::getTitleJa, program::setTitleJa, originalTitle, "ja");
        hasUpdates |= fillTranslationIfEmpty(
            program::getTitleVi, program::setTitleVi, originalTitle, "vi");
        hasUpdates |= fillTranslationIfEmpty(
            program::getTitleId, program::setTitleId, originalTitle, "id");

        if (hasUpdates) {
            log.debug("프로그램 번역 업데이트 완료: {}", originalTitle);
        }

        return hasUpdates;
    }

    /**
     * 신규 프로그램에 모든 언어의 번역을 생성
     *
     * @param program 번역을 생성할 프로그램 엔티티
     * @return 번역이 설정된 프로그램 엔티티
     */
    public EduProgram createWithAllTranslations(EduProgram program) {
        String originalTitle = program.getTitleNm();
        if (originalTitle == null || originalTitle.isBlank()) {
            log.warn("원본 제목이 없어 번역을 건너뜁니다.");
            return program;
        }

        try {
            program.setTitleEn(translateSafely(originalTitle, "en-US"));
            program.setTitleZh(translateSafely(originalTitle, "zh"));
            program.setTitleJa(translateSafely(originalTitle, "ja"));
            program.setTitleVi(translateSafely(originalTitle, "vi"));
            program.setTitleId(translateSafely(originalTitle, "id"));

            log.debug("신규 프로그램 번역 완료: {}", originalTitle);
        } catch (Exception e) {
            log.error("프로그램 번역 중 오류 발생: {}", originalTitle, e);
        }

        return program;
    }

    /**
     * 번역이 비어있는 경우에만 번역을 수행
     */
    private boolean fillTranslationIfEmpty(Supplier<String> getter, Consumer<String> setter,
        String originalText, String targetLanguage) {
        String currentValue = getter.get();
        if (currentValue == null || currentValue.isBlank()) {
            String translated = translateSafely(originalText, targetLanguage);
            setter.accept(translated);
            return true;
        }
        return false;
    }

    /**
     * 안전한 번역 수행 (예외 발생 시 원본 반환)
     */
    private String translateSafely(String text, String targetLanguage) {
        try {
            String translated = translationService.translateText(text, SOURCE_LANGUAGE, targetLanguage);
            return translated != null ? translated : text;
        } catch (Exception e) {
            log.warn("번역 실패 (원본 유지): {} -> {}", text, targetLanguage, e);
            return text;
        }
    }
}
