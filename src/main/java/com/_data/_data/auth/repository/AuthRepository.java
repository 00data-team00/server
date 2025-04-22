package com._data._data.auth.repository;

import com._data._data.auth.entity.Auth;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthRepository extends JpaRepository<Auth, Long> {
    Auth findByEmail(String email);

    // 특정 시각 이전에 만료된 코드를 한 번에 삭제
    long deleteByExpiresAtBefore(LocalDateTime time);
}
