package com._data._data.user.service;

import com._data._data.auth.entity.Auth;
import com._data._data.auth.repository.AuthRepository;
import com._data._data.auth.service.MailService;
import com._data._data.user.dto.SigninRequest;
import com._data._data.user.entity.Users;
import com._data._data.user.exception.EmailAlreadyRegisteredException;
import com._data._data.user.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final MailService mailService;
    private final AuthRepository authRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private static final String DEFAULT_PROFILE_IMAGE = "/uploads/profile/blank.jpg";

    @Transactional
    public boolean sendAuthcode(String email) throws MessagingException {
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyRegisteredException(email);
        }

        String authCode = mailService.sendSimpleMessage(email);
        if(authCode != null){
            Auth auth = authRepository.findByEmail(email);
            if(auth == null)
                authRepository.save(new Auth(email, authCode));
            else
                auth.patch(authCode);
            return true;
        }
        return false;
    }

    public boolean validationAuthcode(String email, String authCode) {
        Auth auth = authRepository.findByEmail(email);
        if (auth == null || auth.isExpired()) {
            if (auth != null) authRepository.delete(auth);
            return false;
        }
        if (auth.getAuthCode().equals(authCode)) {
            authRepository.delete(auth);
            return true;
        }
        return false;
    }

    @Transactional
    public Users register(SigninRequest req) {
        if (userRepository.existsByEmail(req.email())) {
            throw new EmailAlreadyRegisteredException(req.email());
        }

        String hashed = passwordEncoder.encode(req.password());

        Users user = Users.builder()
            .email(req.email())
            .name(req.name())
            .nations(req.nations())
            .password(hashed)
            .translationLang("ko")
            .profileImage(DEFAULT_PROFILE_IMAGE)
            .build();

        return userRepository.save(user);
    }
}
