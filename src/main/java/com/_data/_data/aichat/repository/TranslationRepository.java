package com._data._data.aichat.repository;

import com._data._data.aichat.entity.Translation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TranslationRepository extends JpaRepository<Translation, Long> {
    Optional<Translation> findTranslationByMessageIdAndLang(Long messageId, String lang);
}
