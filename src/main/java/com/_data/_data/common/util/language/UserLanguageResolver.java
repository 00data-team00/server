package com._data._data.common.util.language;

import com._data._data.auth.entity.CustomUserDetails;
import com._data._data.user.entity.Users;
import com._data._data.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 사용자에 따른 언어 설정 조회
 */
@Slf4j
@Component
public class UserLanguageResolver {
    private static final String DEFAULT_LANGUAGE = "ko";

    private final UserRepository userRepository;

    public UserLanguageResolver(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 언어 설정 반환
     * */
    public String getCurrentUserLanguage() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (isAnonymousUser(auth)) {
            log.debug("익명 사용자, 기본 언어 '{}' 사용", DEFAULT_LANGUAGE);
            return DEFAULT_LANGUAGE;
        }

        return getAuthenticatedUserLanguage(auth);
    }

    /**
    * 익명 유저면 기본값 반환
     * */
    private boolean isAnonymousUser(Authentication auth) {
        return auth == null
            || !auth.isAuthenticated()
            || !(auth.getPrincipal() instanceof CustomUserDetails);
    }

    /**
     * 로그인 사용자 설정언어 반환
     * */
    private String getAuthenticatedUserLanguage(Authentication auth) {
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        String email = userDetails.getUsername();

        Users user = userRepository.findByEmailAndIsDeletedFalse(email);
        String language = user.getTranslationLang();

        log.debug("사용자={} 언어 설정={}", email, language);
        return language != null ? language : DEFAULT_LANGUAGE;
    }
}
