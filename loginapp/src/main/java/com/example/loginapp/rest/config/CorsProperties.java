package com.example.loginapp.rest.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.cors")
@Data
public class CorsProperties {
    /**
     * 許可するフロントエンドのオリジン
     */
    private String[] allowedOrigins;
}