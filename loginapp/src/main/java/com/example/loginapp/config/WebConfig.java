package com.example.loginapp.config; // パッケージ名は適切に調整

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // CORS設定：フロントエンドからのリクエストを許可するドメインとメソッドを設定
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:9090", "http://localhost:4200") // クライアントのURL
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowCredentials(true); // セッションを許可する
    }
}
