package com.example.loginapp.rest.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import com.example.loginapp.domain.constants.MessageKeys;

import lombok.Data;

/**
 * セッション関連の設定を保持するクラス。
 */
@Component
@ConfigurationProperties(prefix = "session")
@Data
public class SessionProperties {

    /** セッション有効期限（ミリ秒） */
    private long timeoutMillis;

    /** メッセージソース */
    private MessageSource messageSource;

    public long getTimeoutMillis() {
        return timeoutMillis;
    }

    public void setTimeoutMillis(long timeoutMillis) {
        if (timeoutMillis < 0) {
            String msg = messageSource.getMessage(
                    MessageKeys.ERROR_NEGATIVE_SESSION_TIMEOUT,
                    new Object[] { timeoutMillis },
                    java.util.Locale.JAPANESE);
            throw new IllegalArgumentException(msg);
        }
        this.timeoutMillis = timeoutMillis;
    }
}