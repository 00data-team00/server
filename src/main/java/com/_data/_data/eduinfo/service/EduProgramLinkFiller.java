package com._data._data.eduinfo.service;

import com._data._data.eduinfo.entity.EduProgram;
import com._data._data.eduinfo.repository.EduProgramRepository;
import java.time.Duration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

@Slf4j
@Service
@RequiredArgsConstructor
public class EduProgramLinkFiller {
    private final WebDriver driver;
    private final EduProgramRepository repo;

    // 특수문자 전까지 잘라 검색어 생성
    private String buildQuery(String titleNm) {
        return titleNm.split("[\\[\\(\"“’']")[0];
    }

    @Transactional
    public void fillMissingLinks() {
        List<EduProgram> list = repo.findByAppLinkIsNull();
        log.info("Processing {} programs without links", list.size());

        for (EduProgram ep : list) {
            try {
                processProgram(ep);
                // 각 요청 사이에 잠시 대기 (서버 부하 방지)
                Thread.sleep(1000);
            } catch (Exception e) {
                log.error("Error processing program [{}]: {}", ep.getId(), e.getMessage());
                continue;
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private void processProgram(EduProgram ep) {
        String query = buildQuery(ep.getTitleNm());
        log.debug("Processing program [{}] with query: '{}'", ep.getId(), query);

        driver.get("https://global.seoul.go.kr/web/educ/appr/apprListPage.do?lang=ko");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(By.id("searchFrm")));
            WebElement input = wait.until(ExpectedConditions.elementToBeClickable(By.id("searchtxt")));
            input.clear();
            input.sendKeys(query);
            WebElement searchBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("btnSearch")));
            searchBtn.click();

            wait.until(ExpectedConditions.or(
                ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.boardlistedu")),
                ExpectedConditions.textToBePresentInElement(
                    driver.findElement(By.cssSelector("div.all")), "Total")
            ));

            int total = parseTotalCount();
            if (total == 0) {
                log.warn("No search results for [{}] query: '{}'", ep.getId(), query);
                return;
            }

            WebElement firstLink = findFirstResultLink();
            if (firstLink == null) {
                log.warn("No link element for [{}] query: '{}'", ep.getId(), query);
                return;
            }

            // 클릭 대신 onclick 호출
            String onclick = firstLink.getAttribute("onclick");
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(onclick);
            wait.until(ExpectedConditions.not(
                ExpectedConditions.urlToBe("https://global.seoul.go.kr/web/educ/appr/apprListPage.do?lang=ko")
            ));

            String detailUrl = driver.getCurrentUrl();
            ep.setAppLink(detailUrl);
            repo.saveAndFlush(ep);
            log.info("Filled appLink for [{}]: {}", ep.getId(), detailUrl);

        } catch (TimeoutException e) {
            log.warn("Timeout for [{}] query: '{}'", ep.getId(), query);
        } catch (Exception e) {
            log.error("Unexpected error for [{}]: {}", ep.getId(), e.getMessage());
        }
    }

    private int parseTotalCount() {
        try {
            String text = driver.findElement(By.cssSelector("div.all")).getText();
            Matcher m = Pattern.compile("Total\\s*:\\s*(\\d+)").matcher(text);
            if (m.find()) return Integer.parseInt(m.group(1));
        } catch (Exception e) {
            log.debug("parseTotalCount failed: {}", e.getMessage());
        }
        return -1;
    }

    private WebElement findFirstResultLink() {
        String[] selectors = {
            "div.boardlistedu table tbody tr td.title a",
            "div.boardlistedu ul.list li a",
            "a[onclick*='apprDetail']"
        };
        for (String sel : selectors) {
            List<WebElement> els = driver.findElements(By.cssSelector(sel));
            if (!els.isEmpty()) return els.get(0);
        }
        return null;
    }
    // Stale Element Exception 방지를 위한 재시도 로직
    private String getOnclickWithRetry(WebElement element, int maxRetries) {
        for (int i = 0; i < maxRetries; i++) {
            try {
                return element.getAttribute("onclick");
            } catch (StaleElementReferenceException e) {
                log.debug("StaleElementReferenceException occurred, retrying... ({}/{})", i + 1, maxRetries);
                if (i == maxRetries - 1) {
                    // 마지막 시도에서도 실패하면 요소를 다시 찾아서 시도
                    try {
                        String[] selectors = {
                            "div.boardlistedu table tbody tr td.title a",
                            "div.boardlistedu ul.list li a",
                            "table tbody tr td.title a",
                            "a[onclick*='apprDetail']"
                        };

                        for (String selector : selectors) {
                            List<WebElement> results = driver.findElements(By.cssSelector(selector));
                            if (!results.isEmpty()) {
                                return results.get(0).getAttribute("onclick");
                            }
                        }
                    } catch (Exception ex) {
                        log.error("Failed to re-find element: {}", ex.getMessage());
                    }
                }
                // 짧은 대기 후 재시도
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
        return null;
    }

    // onclick 문자열을 파싱하여 URL 생성 - 더 유연한 파싱 로직
    private String parseOnclickToUrl(String onclick) {
        if (onclick == null || onclick.trim().isEmpty()) {
            return null;
        }

        log.debug("Parsing onclick: {}", onclick);

        // 실제 HTML에서 확인된 패턴: apprDetail('E', 'svc_no', 'post_no', 'center_id', 'KO', 'Y', 'Y', 'Y')
        // 더 유연한 정규식으로 수정
        Pattern pattern = Pattern.compile(
            "apprDetail\\(\\s*'E'\\s*,\\s*'([^']+)'\\s*,\\s*'([^']+)'\\s*(?:,\\s*'[^']*')*\\s*\\)"
        );

        Matcher matcher = pattern.matcher(onclick);

        if (matcher.find()) {
            String svcNo = matcher.group(1);
            String postNo = matcher.group(2);
            String detailUrl = String.format(
                "https://global.seoul.go.kr/web/educ/appr/apprDetailPage.do?lang=ko&svc_no=%s&post_no=%s",
                svcNo, postNo
            );
            log.debug("Generated URL: {}", detailUrl);
            return detailUrl;
        }

        log.warn("Could not parse onclick pattern: {}", onclick);
        return null;
    }

}
