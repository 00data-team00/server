package com._data._data.auth.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Auth {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String authCode;

    private LocalDateTime expiresAt;

    public Auth(String email, String authCode){
        this.email = email;
        this.authCode = authCode;
        // 5분 후 인증코드 만료
        this.expiresAt = LocalDateTime.now().plusMinutes(5);
    }

    public void patch(String authCode){
        this.authCode = authCode;
        this.expiresAt = LocalDateTime.now().plusMinutes(5);
    }

    public boolean isExpired() {
        return expiresAt.isBefore(LocalDateTime.now());
    }
}
