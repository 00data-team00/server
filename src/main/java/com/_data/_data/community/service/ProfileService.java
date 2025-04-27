package com._data._data.community.service;

import com._data._data.user.entity.Users;
import com._data._data.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final UserRepository userRepository;
    private final FileService fileService;

    @Transactional
    public String updateProfileImage(Users user, MultipartFile image) throws IOException {
        String url = "/uploads" + fileService.store(image, "profile", user.getId(), user.getId());
        user.setProfileImage(url);
        userRepository.save(user);
        return url;
    }
}
