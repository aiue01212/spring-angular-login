package com.example.loginapp.rest.service.impl;

import com.example.loginapp.rest.config.SessionProperties;
import com.example.loginapp.rest.service.SessionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;

import static com.example.loginapp.rest.constants.SessionKeys.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link SessionServiceImpl} の動作を検証する単体テスト。
 * <p>
 * 各種セッション属性の設定／削除、および isSessionValid() の全分岐を網羅する。
 * </p>
 */
class SessionServiceImplTest {

    /** テスト対象のサービス */
    private SessionService sessionService;

    /** セッションに関する設定値を保持するプロパティクラス */
    private SessionProperties sessionProperties;

    /** セッションの有効期限（ミリ秒） */
    private static final long SESSION_TIMEOUT_MILLIS = 60_000L;

    /** タイムアウト判定を確実に超えるためのオフセット */
    private static final long TIMEOUT_EXCEEDED_OFFSET = 5_000L;

    /** 無効な属性のテスト用文字列 */
    private static final String INVALID_BOOLEAN_VALUE = "NOT-BOOLEAN";

    /** 無効な属性のテスト用文字列（Longではない） */
    private static final String INVALID_LONG_VALUE = "NOT-LONG";

    /** テスト用ユーザー名 */
    private static final String TEST_USERNAME = "test-user";

    /**
     * テスト前のセットアップ
     */
    @BeforeEach
    void setUp() {
        sessionProperties = new SessionProperties();
        sessionProperties.setTimeoutMillis(SESSION_TIMEOUT_MILLIS);
        sessionService = new SessionServiceImpl(sessionProperties);
    }

    /**
     * createLoginSession() がセッション属性を正しく設定することを確認。
     */
    @Test
    void createLoginSession_SetsAttributes() {
        MockHttpSession session = new MockHttpSession();

        sessionService.createLoginSession(session, TEST_USERNAME);

        assertThat(session.getAttribute(IS_LOGGED_IN)).isEqualTo(true);
        assertThat(session.getAttribute(USERNAME)).isEqualTo(TEST_USERNAME);
        assertThat(session.getAttribute(LOGIN_TIME)).isInstanceOf(Long.class);
    }

    /**
     * invalidateSession() がセッションを無効化することを確認。
     */
    @Test
    void invalidateSession_InvalidatesSession() {
        MockHttpSession session = new MockHttpSession();
        sessionService.invalidateSession(session);

        assertThat(session.isInvalid()).isTrue();
    }

    /**
     * isSessionValid() が有効なセッションの場合 true を返すことを確認。
     */
    @Test
    void isSessionValid_ReturnsTrue_WhenValid() {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(IS_LOGGED_IN, true);
        session.setAttribute(LOGIN_TIME, System.currentTimeMillis());

        boolean result = sessionService.isSessionValid(session);

        assertThat(result).isTrue();
    }

    /**
     * isSessionValid() がログイン情報のない場合 false を返すことを確認。
     */
    @Test
    void isSessionValid_ReturnsFalse_WhenNotLoggedIn() {
        MockHttpSession session = new MockHttpSession();
        boolean result = sessionService.isSessionValid(session);

        assertThat(result).isFalse();
    }

    /**
     * isSessionValid() がタイムアウトしたセッションに対して false を返すことを確認。
     */
    @Test
    void isSessionValid_ReturnsFalse_WhenTimeout() {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(IS_LOGGED_IN, true);
        session.setAttribute(
                LOGIN_TIME,
                System.currentTimeMillis() - SESSION_TIMEOUT_MILLIS - TIMEOUT_EXCEEDED_OFFSET);

        boolean result = sessionService.isSessionValid(session);

        assertThat(result).isFalse();
    }

    /**
     * isSessionValid() が isLoggedIn / loginTime の両方が不正型の場合 false を返すことを確認。
     */
    @Test
    void isSessionValid_ReturnsFalse_WhenAttributeTypeInvalid() {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(IS_LOGGED_IN, INVALID_BOOLEAN_VALUE);
        session.setAttribute(LOGIN_TIME, INVALID_LONG_VALUE);

        boolean result = sessionService.isSessionValid(session);

        assertThat(result).isFalse();
    }

    /**
     * isSessionValid() が loginTime は正しいが isLoggedIn が不正型の場合 false を返すことを確認。
     */
    @Test
    void isSessionValid_ReturnsFalse_WhenIsLoggedInTypeInvalidButLoginTimeValid() {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(IS_LOGGED_IN, INVALID_BOOLEAN_VALUE);
        session.setAttribute(LOGIN_TIME, System.currentTimeMillis());

        boolean result = sessionService.isSessionValid(session);

        assertThat(result).isFalse();
    }

    /**
     * isSessionValid() が isLoggedIn は正しいが loginTime が不正型の場合 false を返すことを確認。
     */
    @Test
    void isSessionValid_ReturnsFalse_WhenIsLoggedInValidButLoginTimeTypeInvalid() {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(IS_LOGGED_IN, true);
        session.setAttribute(LOGIN_TIME, INVALID_LONG_VALUE);

        boolean result = sessionService.isSessionValid(session);

        assertThat(result).isFalse();
    }

    /**
     * isSessionValid() が isLoggedIn = false の場合 false を返すことを確認。
     */
    @Test
    void isSessionValid_ReturnsFalse_WhenLoggedInFlagIsFalse() {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(IS_LOGGED_IN, false);
        session.setAttribute(LOGIN_TIME, System.currentTimeMillis());

        boolean result = sessionService.isSessionValid(session);

        assertThat(result).isFalse();
    }
}