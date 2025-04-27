package com._data._data.aichat.service;

import com._data._data.aichat.entity.Translation;

public interface TranslationService {
    Translation translateMessage(Long messageId, String messageText, String targetLang) throws Exception;

    Translation getTranslation(Long translationId) throws Exception;
}
