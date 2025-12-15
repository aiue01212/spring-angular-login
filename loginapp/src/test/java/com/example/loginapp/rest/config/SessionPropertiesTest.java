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
     * SessionProperties のデフォルトコンストラクタが正しくインスタンスを生成できることを確認するテスト。
     */
    @Test
    void testDefaultConstructor() {
        SessionProperties sp = new SessionProperties();
        assertNotNull(sp);
    }

    /**
     * MessageSource の getter/setter をテストする。
     * Lombok @Data によって生成される getter/setter のカバレッジを確保する。
     */
    @Test
    void testMessageSourceGetterSetter() {
        MessageSource ms = mock(MessageSource.class);
        sessionProperties.setMessageSource(ms);
        assertEquals(ms, sessionProperties.getMessageSource());
    }

    /**
     * Lombok が自動生成する toString, equals, hashCode のカバレッジを取得するテスト。
     */
    @Test
    void testLombokMethods() {
        String str = sessionProperties.toString();
        assertNotNull(str);

        SessionProperties same = sessionProperties;
        SessionProperties copy = new SessionProperties();
        copy.setMessageSource(sessionProperties.getMessageSource());
        copy.setTimeoutMillis(sessionProperties.getTimeoutMillis());
        SessionProperties different = new SessionProperties();
        different.setTimeoutMillis(sessionProperties.getTimeoutMillis() + 1);

        assertTrue(sessionProperties.equals(same));
        assertTrue(sessionProperties.equals(copy));
        assertFalse(sessionProperties.equals(different));
        assertFalse(sessionProperties.equals(null));

        assertEquals(sessionProperties.hashCode(), copy.hashCode());
        assertNotEquals(sessionProperties.hashCode(), different.hashCode());
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
