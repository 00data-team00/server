package com._data._data.aichat.service;

import com._data._data.aichat.dto.TranslateRequestDto;
import com._data._data.aichat.entity.Translation;

public interface TranslationService {
    Translation translateMessage(Long messageId, String messageText, String targetLang) throws Exception;

    void getTranslation(Long translationId) throws Exception;

    Translation getTranslation(TranslateRequestDto translateRequestDto) throws Exception;

    /**
     * @param text 번역할 원문
     * @param sourceLang 원문 언어 코드("ko", "en" 등)
     * @param targetLang 목표 언어 코드
     * @return 번역된 텍스트
     */
    String translateText(String text, String sourceLang, String targetLang) throws Exception;
}
