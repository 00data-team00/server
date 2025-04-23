package com._data._data.auth.repository;

import com._data._data.auth.entity.Auth;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthRepository extends JpaRepository<Auth, Long> {
    Auth findByEmail(String email);
    long deleteByExpiresAtBefore(LocalDateTime time);
}
