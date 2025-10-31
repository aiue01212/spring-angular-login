package com.example.loginapp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

import com.example.loginapp.constants.MessageKeys;

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
        if (timeoutMillis < 0) {
            ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
            messageSource.setBasename("messages");
            messageSource.setDefaultEncoding("UTF-8");

            String msg = messageSource.getMessage(
                    MessageKeys.ERROR_NEGATIVE_SESSION_TIMEOUT,
                    new Object[] { timeoutMillis },
                    java.util.Locale.JAPANESE);
            throw new IllegalArgumentException(msg);
        }
        this.timeoutMillis = timeoutMillis;
    }
}