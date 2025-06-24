package com._data._data.eduinfo.config;


import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SeleniumConfig {

    @Bean(destroyMethod = "quit")
    public WebDriver webDriver() {
        // 1) WebDriverManager로 ChromeDriver 바이너리 다운로드/설치
        WebDriverManager.chromedriver().setup();

        // 2) 헤드리스(headless) 모드 옵션
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");          // 화면 없이 실행
        options.addArguments("--no-sandbox");        // 리눅스 환경 안정화
        options.addArguments("--disable-dev-shm-usage");

        // 3) ChromeDriver 생성
        return new ChromeDriver(options);
    }
}