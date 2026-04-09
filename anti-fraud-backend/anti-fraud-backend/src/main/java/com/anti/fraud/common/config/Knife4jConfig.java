package com.anti.fraud.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Knife4jConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("高校反诈安全知识普及平台API文档")
                        .version("1.0.0")
                        .description("高校反诈安全知识普及平台后端接口文档")
                        .contact(new Contact()
                                .name("Anti-Fraud Team")
                                .email("anti-fraud@example.com")));
    }
}

