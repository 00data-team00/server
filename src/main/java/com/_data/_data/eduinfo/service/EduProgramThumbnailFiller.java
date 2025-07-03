package com._data._data.eduinfo.service;

import com._data._data.eduinfo.entity.EduProgram;
import com._data._data.eduinfo.repository.EduProgramRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EduProgramThumbnailFiller {

    private final WebDriver driver;
    private final EduProgramRepository repo;

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * 썸네일 URL이 없는 프로그램들의 썸네일을 크롤링하여 채움
     * 이미 썸네일이 있는 프로그램은 건너뜀
     */
    public void fillMissingThumbnails() {
        // appLink는 있지만 thumbnailUrl이 없는 프로그램들만 조회
        List<EduProgram> programsNeedingThumbnails = repo.findByAppLinkIsNotNullAndThumbnailUrlIsNull();
        log.info("Processing {} programs without thumbnails", programsNeedingThumbnails.size());

        for (EduProgram program : programsNeedingThumbnails) {
            try {
                String thumbnailUrl = extractThumbnailFromDetailPage(program.getAppLink());
                if (thumbnailUrl != null && !thumbnailUrl.trim().isEmpty()) {
                    program.setThumbnailUrl(thumbnailUrl);
                    repo.saveAndFlush(program);
                    log.info("✅ Filled thumbnail for program [{}]: {}", program.getId(), thumbnailUrl);
                } else {
                    log.warn("❌ No thumbnail found for program [{}]", program.getId());
                }

                // 서버 부하 방지를 위한 대기
                Thread.sleep(1500);

            } catch (Exception e) {
                log.error("💥 Error processing thumbnail for program [{}]: {}", program.getId(), e.getMessage());
                continue;
            }
        }

        log.info("🎉 Thumbnail crawling completed!");
    }

    /**
     * 특정 프로그램의 썸네일 URL을 수동으로 업데이트 (강제 업데이트)
     */
    @Transactional
    public void updateThumbnailForProgram(Long programId) {
        EduProgram program = repo.findById(programId)
            .orElseThrow(() -> new IllegalArgumentException("Program not found: " + programId));

        if (program.getAppLink() == null || program.getAppLink().trim().isEmpty()) {
            log.warn("Program [{}] has no appLink", programId);
            return;
        }

        try {
            String thumbnailUrl = extractThumbnailFromDetailPage(program.getAppLink());
            if (thumbnailUrl != null && !thumbnailUrl.trim().isEmpty()) {
                program.setThumbnailUrl(thumbnailUrl);
                repo.saveAndFlush(program);
                log.info("✅ Updated thumbnail for program [{}]: {}", programId, thumbnailUrl);
            } else {
                log.warn("❌ No thumbnail found for program [{}]", programId);
            }
        } catch (Exception e) {
            log.error("💥 Error updating thumbnail for program [{}]: {}", programId, e.getMessage());
            throw e;
        }
    }

    /**
     * 서울시 외국인 포털 상세 페이지에서 썸네일 이미지 URL 추출
     * 실제 HTML 구조에 기반한 정확한 셀렉터 사용
     */
    private String extractThumbnailFromDetailPage(String detailUrl) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        try {
            log.debug("🔍 Extracting thumbnail from Seoul Portal: {}", detailUrl);
            driver.get(detailUrl);

            // 페이지 로딩 대기
            wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));

            // 🎯 실제 페이지 구조에 맞는 정확한 셀렉터들
            String[] accurateSelectors = {
                // 1. 메인 프로그램 이미지 (가장 우선순위)
                "dt.img_box img",                           // 실제 구조: <dt class="img_box"><img src="...">
                ".board_detail dt.img_box img",             // 더 구체적인 경로

                // 2. 상세 내용 영역의 이미지들
                ".board_detail .content img",               // 상세 내용의 이미지
                "div.content img",                          // 컨텐츠 영역 이미지

                // 3. 첨부파일 관련 이미지 (실제로는 다운로드 링크지만 확인)
                ".file a[href*='fileDownLoad']",            // 첨부파일 링크 (이미지일 수 있음)

                // 4. 일반적인 이미지들 (크기 필터링 적용)
                ".boardview img",                           // 게시판 뷰 내 이미지
                ".detail img",                              // 상세 영역 이미지

                // 5. 백업용 셀렉터들
                "img[src*='/contents/edmg/prog/']",         // 프로그램 이미지 경로 패턴
                "img[src*='.jpg'], img[src*='.jpeg'], img[src*='.png']"  // 이미지 파일 확장자
            };

            for (String selector : accurateSelectors) {
                try {
                    List<WebElement> elements = driver.findElements(By.cssSelector(selector));
                    log.debug("Found {} elements with selector: {}", elements.size(), selector);

                    for (WebElement element : elements) {
                        String imageUrl = extractImageUrl(element, selector);

                        if (isValidSeoulPortalImage(imageUrl)) {
                            String absoluteUrl = makeAbsoluteUrl(imageUrl, detailUrl);
                            log.info("🎯 Found valid thumbnail: {} (from selector: {})", absoluteUrl, selector);
                            return absoluteUrl;
                        }
                    }
                } catch (Exception e) {
                    log.debug("Selector '{}' failed: {}", selector, e.getMessage());
                    continue;
                }
            }

            // 첨부파일에서 이미지 찾기 (특별 처리)
            String attachmentImage = checkAttachmentFiles();
            if (attachmentImage != null) {
                return attachmentImage;
            }

            log.warn("❌ No thumbnail image found on Seoul Portal page: {}", detailUrl);
            return null;

        } catch (TimeoutException e) {
            log.warn("⏰ Timeout while loading Seoul Portal page: {}", detailUrl);
            return null;
        } catch (Exception e) {
            log.error("💥 Error extracting thumbnail from Seoul Portal {}: {}", detailUrl, e.getMessage());
            return null;
        }
    }

    /**
     * 요소에서 이미지 URL 추출 (셀렉터 타입에 따라 처리)
     */
    private String extractImageUrl(WebElement element, String selector) {
        try {
            String tagName = element.getTagName().toLowerCase();

            // img 태그인 경우
            if ("img".equals(tagName)) {
                String src = element.getAttribute("src");
                if (src != null && !src.trim().isEmpty()) {
                    return src;
                }

                // lazy loading 대비
                String dataSrc = element.getAttribute("data-src");
                if (dataSrc != null && !dataSrc.trim().isEmpty()) {
                    return dataSrc;
                }
            }

            // a 태그인 경우 (첨부파일 링크)
            else if ("a".equals(tagName) && selector.contains("fileDownLoad")) {
                String href = element.getAttribute("href");
                String text = element.getText().toLowerCase();

                // 파일명이 이미지 확장자를 포함하는지 확인
                if (href != null && (text.contains(".jpg") || text.contains(".jpeg") ||
                    text.contains(".png") || text.contains(".gif"))) {
                    // 첨부파일 다운로드 URL을 직접 이미지 URL로 사용
                    return href;
                }
            }

        } catch (Exception e) {
            log.debug("Failed to extract URL from element: {}", e.getMessage());
        }

        return null;
    }

    /**
     * 첨부파일에서 이미지 찾기
     */
    private String checkAttachmentFiles() {
        try {
            // 첨부파일 목록에서 이미지 파일 찾기
            List<WebElement> fileLinks = driver.findElements(By.cssSelector(".file a[href*='fileDownLoad']"));

            for (WebElement link : fileLinks) {
                String fileName = link.getText().toLowerCase();
                String href = link.getAttribute("href");

                // 이미지 파일인지 확인
                if ((fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") ||
                    fileName.endsWith(".png") || fileName.endsWith(".gif")) &&
                    href != null) {

                    log.debug("📎 Found image attachment: {} -> {}", fileName, href);
                    return href; // 첨부파일 다운로드 URL 반환
                }
            }
        } catch (Exception e) {
            log.debug("Failed to check attachment files: {}", e.getMessage());
        }

        return null;
    }

    /**
     * 서울시 포털 특화 이미지 유효성 검증
     */
    private boolean isValidSeoulPortalImage(String url) {
        if (url == null || url.trim().isEmpty()) {
            return false;
        }

        String lowerUrl = url.toLowerCase();

        // ❌ 제외할 이미지들
        if (lowerUrl.startsWith("data:") ||
            lowerUrl.contains("placeholder") ||
            lowerUrl.contains("blank") ||
            lowerUrl.contains("noimage") ||
            lowerUrl.contains("default") ||
            lowerUrl.contains("1x1") ||
            lowerUrl.contains("icon") ||
            lowerUrl.contains("bullet") ||
            lowerUrl.contains("bg_") ||
            lowerUrl.endsWith(".svg")) {
            return false;
        }

        // ❌ 너무 작은 이미지 (아이콘 등)
        if (lowerUrl.contains("16x16") ||
            lowerUrl.contains("24x24") ||
            lowerUrl.contains("32x32") ||
            lowerUrl.contains("small")) {
            return false;
        }

        // ✅ 유효한 이미지 확장자 또는 첨부파일 다운로드 링크
        return lowerUrl.contains(".jpg") ||
            lowerUrl.contains(".jpeg") ||
            lowerUrl.contains(".png") ||
            lowerUrl.contains(".gif") ||
            lowerUrl.contains(".webp") ||
            lowerUrl.contains("filedownload"); // 첨부파일 다운로드 링크도 허용
    }

    /**
     * 서울시 포털의 절대 URL 생성
     */
    private String makeAbsoluteUrl(String imageUrl, String baseUrl) {
        if (imageUrl == null) return null;

        // 이미 절대 URL인 경우
        if (imageUrl.startsWith("http://") || imageUrl.startsWith("https://")) {
            return imageUrl;
        }

        // 서울시 포털 도메인으로 절대 URL 생성
        if (imageUrl.startsWith("/")) {
            return "https://global.seoul.go.kr" + imageUrl;
        }

        // 상대 경로인 경우
        try {
            java.net.URL base = new java.net.URL(baseUrl);
            java.net.URL absolute = new java.net.URL(base, imageUrl);
            return absolute.toString();
        } catch (Exception e) {
            log.debug("Failed to make absolute URL, using fallback: {}", imageUrl);
            return "https://global.seoul.go.kr/" + imageUrl;
        }
    }

    /**
     * 통계 정보 조회 및 출력
     */
    public void printThumbnailStats() {
        long totalPrograms = repo.count();
        long programsWithLinks = repo.countByAppLinkIsNotNull();
        long programsWithThumbnails = repo.countByThumbnailUrlIsNotNull();
        long programsNeedingThumbnails = repo.countByAppLinkIsNotNullAndThumbnailUrlIsNull();

        log.info("📊 === Thumbnail Statistics ===");
        log.info("📚 Total programs: {}", totalPrograms);
        log.info("🔗 Programs with appLink: {}", programsWithLinks);
        log.info("🖼️ Programs with thumbnails: {}", programsWithThumbnails);
        log.info("❓ Programs needing thumbnails: {}", programsNeedingThumbnails);
        log.info("📈 Completion rate: {:.1f}%",
            programsWithLinks > 0 ? (double) programsWithThumbnails / programsWithLinks * 100 : 0);
    }

    /**
     * 모든 썸네일 URL 초기화 (개발/테스트용)
     */
    @Transactional
    public void clearAllThumbnails() {
        int updated = repo.clearAllThumbnailUrls();
        log.info("🗑️ Cleared thumbnails for {} programs", updated);
    }
}