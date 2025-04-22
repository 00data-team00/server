package com._data._data.user.service;

import com._data._data.auth.entity.Auth;
import com._data._data.auth.repository.AuthRepository;
import com._data._data.auth.service.MailService;
import com._data._data.user.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final MailService mailService;
    private final AuthRepository authRepository;

    @Transactional
    public boolean sendAuthcode(String email) throws MessagingException {
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
}
