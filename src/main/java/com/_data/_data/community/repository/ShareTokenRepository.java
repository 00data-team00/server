package com._data._data.community.repository;

import com._data._data.community.entity.ShareToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface ShareTokenRepository extends JpaRepository<ShareToken, Long> {
    Optional<ShareToken> findByTokenAndContentTypeAndExpiresAtAfter(
        String token, String contentType, LocalDateTime now);

    Optional<ShareToken> findByToken(String token);
}