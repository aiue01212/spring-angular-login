package com.example.loginapp.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Webアプリケーション全体のCORS設定を行うクラス。
 */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    /** CORS設定用のプロパティ。 */
    private final CorsProperties corsProperties;

    /** 許可するHTTPメソッド */
    private static final String[] ALLOWED_METHODS = { "GET", "POST", "PUT", "DELETE" };

    /**
     * CORS設定を追加する。
     *
     * @param registry {@link CorsRegistry} CORS設定用のレジストリ
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(corsProperties.getAllowedOrigins())
                .allowedMethods(ALLOWED_METHODS)
                .allowCredentials(true);
    }
}
