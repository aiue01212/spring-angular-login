package com.example.loginapp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * セッション関連の設定を保持するクラス。
 */
@Configuration
@ConfigurationProperties(prefix = "session")
public class SessionProperties {

    /** セッション有効期限（ミリ秒） */
    private long timeoutMillis;

    public long getTimeoutMillis() {
        return timeoutMillis;
    }

    public void setTimeoutMillis(long timeoutMillis) {
        this.timeoutMillis = timeoutMillis;
    }
}