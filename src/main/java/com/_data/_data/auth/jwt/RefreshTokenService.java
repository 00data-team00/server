package com._data._data.auth.jwt;

import com._data._data.auth.dto.LoginResponse;
import com._data._data.auth.entity.RefreshToken;
import com._data._data.auth.exception.TokenExpiredException;
import com._data._data.auth.exception.TokenNotFoundException;
import com._data._data.auth.jwt.JwtTokenProvider;
import com._data._data.auth.repository.RefreshTokenRepository;
import com._data._data.user.entity.Users;
import com._data._data.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public LoginResponse refreshAccessToken(String refreshTokenValue) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenValue)
            .orElseThrow(() -> new TokenNotFoundException("유효하지 않은 Refresh Token입니다."));

        if (refreshToken.isExpired()) {
            refreshTokenRepository.delete(refreshToken);
            throw new TokenExpiredException("만료된 Refresh Token입니다.");
        }

        Users user = userRepository.findById(refreshToken.getUserId())
            .orElseThrow(() -> new TokenNotFoundException("사용자를 찾을 수 없습니다."));

        return jwtTokenProvider.generateTokenDto(user.getId(), user.getEmail());
    }

    @Transactional
    public void logout(String refreshTokenValue) {
        refreshTokenRepository.deleteByToken(refreshTokenValue);
    }
}
