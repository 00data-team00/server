package com._data._data.auth.service;

import com._data._data.auth.entity.CustomUserDetails;
import com._data._data.user.entity.Users;
import com._data._data.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
//@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        try {
            Long id = Long.parseLong(userId);
            Users user = memberRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + userId));
            return new CustomUserDetails(user);
        } catch (NumberFormatException e) {
            // userId가 아닌 경우 email로 처리
            Users user = memberRepository.findByEmail(userId);
            if (user == null) {
                throw new UsernameNotFoundException("등록된 이메일이 없습니다: " + userId);
            }
            return new CustomUserDetails(user);
        }
    }
}
