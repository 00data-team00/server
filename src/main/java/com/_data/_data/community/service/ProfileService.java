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
    private final FirebaseFileService firebaseFileService; // FileService -> FirebaseFileService로 변경

    @Transactional
    public String updateProfileImage(Users user, MultipartFile image) throws IOException {
        // 기존 프로필 이미지가 있다면 삭제
        if (user.getProfileImage() != null && !user.getProfileImage().isEmpty()) {
            String oldFileName = extractFileNameFromUrl(user.getProfileImage());
            firebaseFileService.delete(oldFileName);
        }
        String url = "/uploads" + firebaseFileService.store(image, "profile", user.getId(), user.getId());
        user.setProfileImage(url);
        userRepository.save(user);
        return url;
    }

    /**
     * Firebase Storage URL에서 파일 경로 추출
     */
    private String extractFileNameFromUrl(String url) {
        if (url == null || url.isEmpty()) {
            return null;
        }

        try {
            String[] parts = url.split("/");
            if (parts.length >= 2) {
                return parts[parts.length - 2] + "/" + parts[parts.length - 1].split("\\?")[0];
            }
        } catch (Exception e) {
            System.err.println("URL에서 파일명 추출 실패: " + e.getMessage());
        }

        return null;
    }
}
