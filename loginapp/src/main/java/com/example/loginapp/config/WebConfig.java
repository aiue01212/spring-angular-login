package com.example.loginapp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Webアプリケーション全体のCORS設定を行うクラス。
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /** フロントエンドの許可するオリジン */
    private static final String[] ALLOWED_ORIGINS = { "http://localhost:9090", "http://localhost:4200" };

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
                .allowedOrigins(ALLOWED_ORIGINS)
                .allowedMethods(ALLOWED_METHODS)
                .allowCredentials(true); // セッション情報を許可
    }
}
