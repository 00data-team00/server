package com._data._data.auth.jwt;

import com._data._data.auth.dto.LoginResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtTokenProvider {
    private final Key key;
    private final long accessTokenExpMillis;
    private final long refreshTokenExpMillis;

    public JwtTokenProvider(
        @Value("${jwt.secret}") String secretKey,
        @Value("${jwt.expiration_time}") long accessTokenExpMillis,
        @Value("${jwt.refresh_token_expiration_time}") long refreshTokenExpMillis

    ) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenExpMillis = accessTokenExpMillis;
        this.refreshTokenExpMillis = refreshTokenExpMillis;
    }

    public String createAccessToken(Long userId, String email) {
        return createToken(userId, email, accessTokenExpMillis);
    }

    public String createRefreshToken(Long userId, String email) {
        return createToken(userId, email, refreshTokenExpMillis);
    }

    private String createToken(Long userId, String email, long expireMillis) {
        Claims claims = Jwts.claims();
        claims.put("userId", userId);
        claims.put("email", email);

        Instant now = Instant.now();
        Instant expiration = now.plusMillis(expireMillis);

        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(Date.from(now))
            .setExpiration(Date.from(expiration))
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();
    }

    public LoginResponse generateTokenDto(Long userId, String email) {
        String accessToken  = createAccessToken(userId, email);
        String refreshToken = createRefreshToken(userId, email);
        return new LoginResponse(accessToken, refreshToken);
    }

    public Long getUserId(String token) {
        return parseClaims(token).get("userId", Long.class);
    }


    public Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e);
        }
        return false;
    }
}
