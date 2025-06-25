package com._data._data.auth.service;

import com._data._data.auth.dto.LoginRequest;
import com._data._data.auth.dto.LoginResponse;
import com._data._data.auth.exception.EmailNotFoundException;
import com._data._data.auth.exception.InvalidPasswordException;
import com._data._data.auth.jwt.JwtTokenProvider;
import com._data._data.user.entity.Users;
import com._data._data.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    @Transactional
    public LoginResponse login(LoginRequest dto) {
        Users user = userRepository.findByEmailAndIsDeletedFalse(dto.email());
        if (user == null) {
            throw new EmailNotFoundException(dto.email());
        }

        if (!passwordEncoder.matches(dto.password(), user.getPassword())) {
            throw new InvalidPasswordException();
        }

        return jwtTokenProvider.generateTokenDto(
            user.getId(),
            user.getEmail()
        );
    }
}
