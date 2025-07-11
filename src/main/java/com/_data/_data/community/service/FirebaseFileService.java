package com._data._data.community.service;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.firebase.cloud.StorageClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

import static org.springframework.util.StringUtils.getFilenameExtension;

@Service
@RequiredArgsConstructor
public class FirebaseFileService {

    /**
     * 파일을 Firebase Storage에 업로드
     * @param file 업로드할 파일
     * @param dirName 디렉토리명 ("post", "profile", "eduinfo")
     * @param userId 사용자 ID
     * @param postId 포스트 ID (선택적)
     * @return 업로드된 파일의 다운로드 URL
     */
    public String store(MultipartFile file, String dirName, Long userId, Long postId) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어있습니다.");
        }

        String extension = getFilenameExtension(file.getOriginalFilename());
        String fileName = generateFileName(dirName, userId, postId, extension);
        String objectName = dirName + "/" + fileName;

        Storage storage = StorageClient.getInstance().bucket().getStorage();
        BlobId blobId = BlobId.of(StorageClient.getInstance().bucket().getName(), objectName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
            .setContentType(file.getContentType())
            .build();

        storage.create(blobInfo, file.getBytes());

        // 공개 다운로드 URL 생성
        return generateDownloadUrl(objectName);
    }

    /**
     * 파일명 생성
     */
    private String generateFileName(String dirName, Long userId, Long postId, String extension) {
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        String timestamp = String.valueOf(System.currentTimeMillis());

        if (postId != null) {
            return String.format("%s_%s_%s_%s_%s.%s",
                dirName, userId, postId, timestamp, uniqueId, extension);
        } else {
            return String.format("%s_%s_%s_%s.%s",
                dirName, userId, timestamp, uniqueId, extension);
        }
    }

    /**
     * 다운로드 URL 생성 (공개 URL)
     */
    private String generateDownloadUrl(String objectName) {
        String bucketName = StorageClient.getInstance().bucket().getName();
        // Firebase Storage 공개 URL 형식
        return String.format("https://firebasestorage.googleapis.com/v0/b/%s/o/%s?alt=media",
            bucketName, objectName.replace("/", "%2F"));
    }

    /**
     * 파일 삭제
     * @param fileName 삭제할 파일명 (전체 경로)
     */
    public void delete(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return;
        }

        try {
            Storage storage = StorageClient.getInstance().bucket().getStorage();
            BlobId blobId = BlobId.of(StorageClient.getInstance().bucket().getName(), fileName);
            storage.delete(blobId);
        } catch (Exception e) {
            System.err.println("Firebase 파일 삭제 실패: " + e.getMessage());
        }
    }

    /**
     * 프로필 이미지 전용 업로드
     */
    public String storeProfile(MultipartFile file, Long userId) throws IOException {
        return store(file, "profile", userId, null);
    }

    /**
     * 교육 정보 이미지 전용 업로드
     */
    public String storeEduInfo(MultipartFile file, Long id) throws IOException {
        return store(file, "eduinfo", id, null);
    }

    /**
     * 기본 교육 정보 이미지 경로 반환
     */
    public String getDefaultEduInfoImage() {
        // Firebase Storage의 기본 이미지 URL 또는 CDN URL
        return "https://your-default-image-url.com/blank.jpg";
    }
}