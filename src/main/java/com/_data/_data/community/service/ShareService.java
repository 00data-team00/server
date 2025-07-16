package com._data._data.community.service;


import com._data._data.community.entity.ShareToken;
import com._data._data.community.repository.ShareTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShareService {

    private final ShareTokenRepository shareTokenRepository;

    public String createPostShareToken(Long postId) {
        String token = UUID.randomUUID().toString();

        ShareToken shareToken = ShareToken.builder()
            .token(token)
            .contentType("POST")
            .contentId(postId)
            .expiresAt(LocalDateTime.now().plusDays(30)) // 30일 후 만료
            .createdAt(LocalDateTime.now())
            .build();

        shareTokenRepository.save(shareToken);
        return token;
    }

    public String createProfileShareToken(Long userId) {
        String token = UUID.randomUUID().toString();

        ShareToken shareToken = ShareToken.builder()
            .token(token)
            .contentType("PROFILE")
            .contentId(userId)
            .expiresAt(LocalDateTime.now().plusDays(30))
            .createdAt(LocalDateTime.now())
            .build();

        shareTokenRepository.save(shareToken);
        return token;
    }

    public ShareToken validateToken(String token, String expectedType) {
        return shareTokenRepository.findByTokenAndContentTypeAndExpiresAtAfter(
                token, expectedType, LocalDateTime.now())
            .orElse(null);
    }

    public Long getPostIdByToken(String token) {
        ShareToken shareToken = validateToken(token, "POST");
        return shareToken != null ? shareToken.getContentId() : null;
    }

    public Long getUserIdByToken(String token) {
        ShareToken shareToken = validateToken(token, "PROFILE");
        return shareToken != null ? shareToken.getContentId() : null;
    }
}