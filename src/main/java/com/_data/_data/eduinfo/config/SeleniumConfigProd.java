package com._data._data.eduinfo.config;

import java.net.URL;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("prod")
public class SeleniumConfigProd {
    @Bean(destroyMethod = "quit")

    public WebDriver webDriver(@Value("${selenium.url}") String seleniumUrl) throws Exception {
        ChromeOptions opts = new ChromeOptions();
        opts.addArguments("--headless","--no-sandbox","--disable-dev-shm-usage");
        return new RemoteWebDriver(new URL(seleniumUrl), opts);
    }
}
