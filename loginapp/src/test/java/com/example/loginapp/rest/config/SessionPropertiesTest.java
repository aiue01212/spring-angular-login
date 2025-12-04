package com.example.loginapp.rest.config;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;

import com.example.loginapp.domain.constants.MessageKeys;
import com.example.loginapp.rest.constants.SessionPropertiesConstants;

/**
 * {@link SessionProperties} のテストクラス。
 */
class SessionPropertiesTest {

    /** モック化したメッセージソース */
    private MessageSource messageSource;

    /** テスト対象の SessionProperties インスタンス */
    private SessionProperties sessionProperties;

    /**
     * 各テストメソッド実行前に呼ばれるセットアップ処理。
     * MessageSource をモック化し、SessionProperties に注入する。
     */
    @BeforeEach
    void setUp() {
        messageSource = mock(MessageSource.class);
        sessionProperties = new SessionProperties();
        sessionProperties.setMessageSource(messageSource);
    }

    /**
     * セッションタイムアウトに正常値を設定した場合のテスト。
     * 正しく値が設定されることを検証する。
     */
    @Test
    void testSetTimeoutMillis_normal() {
        sessionProperties.setTimeoutMillis(SessionPropertiesConstants.TIMEOUT_NORMAL);
        assertEquals(SessionPropertiesConstants.TIMEOUT_NORMAL, sessionProperties.getTimeoutMillis());
    }

    /**
     * セッションタイムアウトに負の値を設定した場合のテスト。
     * IllegalArgumentException が発生し、返されるメッセージが正しいことを検証する。
     */
    @Test
    void testSetTimeoutMillis_negative_throwsException() {
        when(messageSource.getMessage(
                eq(MessageKeys.ERROR_NEGATIVE_SESSION_TIMEOUT),
                any(),
                eq(SessionPropertiesConstants.LOCALE_JAPANESE)))
                .thenReturn(SessionPropertiesConstants.MSG_NEGATIVE_TIMEOUT);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> sessionProperties.setTimeoutMillis(SessionPropertiesConstants.TIMEOUT_NEGATIVE));

        assertEquals(SessionPropertiesConstants.MSG_NEGATIVE_TIMEOUT, ex.getMessage());
    }
}
