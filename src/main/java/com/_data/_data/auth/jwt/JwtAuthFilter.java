package com._data._data.auth.jwt;

import com._data._data.auth.config.SecurityConstant;
import com._data._data.auth.entity.CustomUserDetails;
import com._data._data.auth.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        String method = request.getMethod();
        log.info("JwtAuthFilter.shouldNotFilter - Path: '{}', Method: '{}'", path, method);

        boolean shouldSkip = false;
        for (String pattern : SecurityConstant.WHITE_LIST) {
            if (new AntPathMatcher().match(pattern, path)) {
                shouldSkip = true;
                break;
            }
        }

        log.info("JwtAuthFilter.shouldNotFilter - Should Skip Filter: {}", shouldSkip);
        return shouldSkip;
    }

    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");
        String path = request.getServletPath();
        String method = request.getMethod();
        log.info("JwtAuthFilter.doFilterInternal - Processing request: Path: '{}', Method: '{}'", path, method);

        log.info("JwtAuthFilter.doFilterInternal - Authorization Header: {}",
            authorizationHeader != null ? "Present" : "Not Present");

        //JWT가 헤더에 있는 경우
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            //JWT 유효성 검증
            if (jwtTokenProvider.validateToken(token)) {
                Long userId = jwtTokenProvider.getUserId(token);

                //유저와 토큰 일치 시 userDetails 생성
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(userId.toString());

                if (userDetails != null) {
                    //UserDetsils, Password, Role -> 접근권한 인증 Token 생성
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

                    if (userDetails instanceof CustomUserDetails cud) {
                        log.info("Authenticated User - Email: {}, Name: {}",
                            cud.getUser().getEmail(),
                            cud.getUser().getName()
                        );
                    }
                }
            }
        }
        log.info("JwtAuthFilter.doFilterInternal - Finished processing. Passing to next filter");
        filterChain.doFilter(request, response);
    }



}
