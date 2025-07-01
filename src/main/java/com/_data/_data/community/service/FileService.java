package com._data._data.community.service;

import static org.springframework.util.StringUtils.*;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class FileService {
    @Value("${app.upload.dir}")
    private String uploadDir;

    /**
     * @param file    업로드된 파일
     * @param userId  파일명에 포함할 사용자 ID
     * @param postId  파일명에 포함할 포스트 ID
     * @return        클라이언트에 반환할 URL 경로
     */
    /** dirName: "post" or "profile" */
    public String store(MultipartFile file, String dirName, Long userId, Long postId) throws IOException {
        String ext = getFilenameExtension(file.getOriginalFilename());
        String filename = dirName + "_" + userId + "_" + postId + "." + ext;

        Path root = Paths.get(System.getProperty("user.dir")).resolve(uploadDir).resolve(dirName);
        Files.createDirectories(root);

        Path target = root.resolve(filename);
        file.transferTo(target.toFile());

        return String.format("/%s/%s", dirName, filename);
    }

    /**
     * 파일 삭제
     * @param relativePath /post/파일명 또는 /profile/파일명 이런 형식
     */
    public void delete(String relativePath) {
        if (relativePath == null || relativePath.isEmpty()) {
            return;
        }
        try {
            Path root = Paths.get(System.getProperty("user.dir")).resolve(uploadDir);
            Path target = root.resolve(relativePath.replaceFirst("^/", ""));

            if (Files.exists(target)) {
                Files.delete(target);
            }
        } catch (IOException e) {
            // 로깅만 하고 넘어가기
            System.err.println("파일 삭제 실패: " + e.getMessage());
        }
    }


    /** 프로필 전용 오버로드 */
    public String storeProfile(MultipartFile file, Long userId) throws IOException {
        return store(file, "profile", userId, userId);
    }

    @PostConstruct
    public void init() throws IOException {
        Path root = Paths.get(System.getProperty("user.dir")).resolve(uploadDir);
        Files.createDirectories(root.resolve("post"));
        Files.createDirectories(root.resolve("profile"));
        Files.createDirectories(root.resolve("eduinfo"));


    }

    /**
     * eduinfo 디렉토리의 기본 이미지 경로 반환
     */
    public String getDefaultEduInfoImage() {
        return "/eduinfo/blank.jpg";
    }

    /**
     * eduinfo용 파일 저장
     */
    public String storeEduInfo(MultipartFile file, Long id) throws IOException {
        return store(file, "eduinfo", id, id);
    }

}
