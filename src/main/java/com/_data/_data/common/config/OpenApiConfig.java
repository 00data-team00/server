package com._data._data.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI instagramInsightOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Instagram Insight API")
                .version("v1")
                .description("인스타그램 인사이트 조회 및 게시 API 문서"));
    }
}
