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
                .title("00data API")
                .version("v1")
                .description("00data API 문서"));
    }
}
