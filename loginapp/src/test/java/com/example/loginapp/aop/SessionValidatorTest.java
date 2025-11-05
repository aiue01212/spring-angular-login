package com.example.loginapp.aop;

import com.example.loginapp.annotation.SessionRequired;
import com.example.loginapp.config.SessionProperties;
import com.example.loginapp.constants.MessageKeys;
import com.example.loginapp.dto.ErrorResponse;
import com.example.loginapp.dto.SessionCheckResponse;
import com.example.loginapp.dto.SuccessResponse;

import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpSession;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static com.example.loginapp.constants.MessageKeys.*;
import static com.example.loginapp.constants.SessionKeys.*;

/**
 * {@link SessionValidator} の動作を検証する単体テスト。
 * <p>
 * 未ログイン・セッション期限切れ・正常ケースの3パターンを検証する。
 * </p>
 */
class SessionValidatorTest {

        /**
         * テスト対象の {@link SessionValidator} インスタンス。
         */
        private SessionValidator validator;

        /**
         * メッセージ解決に使用する {@link MessageSource} のモック。
         */
        private MessageSource messageSource;

        /**
         * セッションタイムアウト値などを保持する {@link SessionProperties}。
         */
        private SessionProperties sessionProperties;

        /** セッション有効期限 */
        private static final long SESSION_TIMEOUT_MILLIS = 60_000L;

        /** タイムアウト検証用：有効期限を超過させる時間（2分 = 120_000ms） */
        private static final long EXPIRED_SESSION_OFFSET_MILLIS = SESSION_TIMEOUT_MILLIS * 2;

        /** joinPoint.proceed() の呼び出し回数（正常系のみ1回） */
        private static final int EXPECTED_PROCEED_INVOCATION_COUNT = 1;

        /**
         * テストや処理中に使用されるメッセージ定数をまとめた定義。
         */
        private static final String MSG_NOT_LOGGED_IN = "ログインしていません";
        private static final String MSG_SESSION_EXPIRED = "セッションがタイムアウトしました";
        private static final String MSG_SUCCESS_PROCESS = "成功しました";
        private static final String MSG_OK = "OK";
        private static final String MSG_SESSION_NOEXIT = "セッションが存在しません";
        private static final String MSG_NON_RESPONSE_ENTITY_RESULT = "非ResponseEntityの結果";
        private static final String ERROR_PROCEED_JOINPOINT_FAILED = "ジョインポイントの処理中にエラーが発生しました";
        private static final String ERROR_EXCEPTION_MESSAGE_NULL = "例外メッセージが null です";
        private static final String MSG_NON_RESPONSE_SESSION_CHECK = "非SessionCheckResponse";
        private static final String MSG_EXCEPTION_SHOULD_NOT_BE_NULL = "Exception message should not be null";
        private static final String MSG_EXCEPTION_SHOULD_CONTAIN = "Expected message to contain: \"%s\", but was: \"%s\"";

        @BeforeEach
        void setUp() {
                messageSource = mock(MessageSource.class);
                sessionProperties = new SessionProperties();
                sessionProperties.setTimeoutMillis(SESSION_TIMEOUT_MILLIS);
                validator = new SessionValidator(messageSource, sessionProperties);

                when(messageSource.getMessage(eq(ERROR_NOT_LOGGED_IN), any(), any(Locale.class)))
                                .thenReturn(MSG_NOT_LOGGED_IN);
                when(messageSource.getMessage(eq(ERROR_SESSION_EXPIRED), any(), any(Locale.class)))
                                .thenReturn(MSG_SESSION_EXPIRED);
                when(messageSource.getMessage(eq(SUCCESS_PROCESS), any(), any(Locale.class)))
                                .thenReturn(MSG_SUCCESS_PROCESS);
                when(messageSource.getMessage(eq(MessageKeys.ERROR_PROCEED_JOINPOINT_FAILED), any(), any(Locale.class)))
                                .thenReturn(ERROR_PROCEED_JOINPOINT_FAILED);
        }

        /** 未ログインの場合、401エラーが返ることを確認 */
        @Test
        void testNotLoggedIn() throws Exception {
                MockHttpSession session = new MockHttpSession();
                ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
                SessionRequired sessionRequired = mock(SessionRequired.class);

                when(joinPoint.getArgs()).thenReturn(new Object[] { session });

                ResponseEntity<SessionCheckResponse> response = (ResponseEntity<SessionCheckResponse>) validator
                                .checkSession(joinPoint, sessionRequired);

                assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatusCode().value());
                assertTrue(response.getBody() instanceof ErrorResponse);
                assertEquals(MSG_NOT_LOGGED_IN, ((ErrorResponse) response.getBody()).getError());

                try {
                        verify(joinPoint, never()).proceed();
                } catch (Throwable ignored) {
                }
        }

        /** セッション期限切れの場合、401エラーが返ることを確認 */
        @Test
        void testSessionExpired() throws Exception {
                MockHttpSession session = new MockHttpSession();
                session.setAttribute(IS_LOGGED_IN, true);
                session.setAttribute(LOGIN_TIME, System.currentTimeMillis() - EXPIRED_SESSION_OFFSET_MILLIS);
                ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
                SessionRequired sessionRequired = mock(SessionRequired.class);

                when(joinPoint.getArgs()).thenReturn(new Object[] { session });

                ResponseEntity<SessionCheckResponse> response = (ResponseEntity<SessionCheckResponse>) validator
                                .checkSession(joinPoint, sessionRequired);

                assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatusCode().value());
                assertTrue(response.getBody() instanceof ErrorResponse);
                assertEquals(MSG_SESSION_EXPIRED,
                                ((ErrorResponse) response.getBody()).getError());

                try {
                        verify(joinPoint, never()).proceed();
                } catch (Throwable ignored) {
                }
        }

        /** 有効なセッションの場合、joinPoint.proceed() の結果が返ることを確認 */
        @Test
        void testValidSession() throws Exception {
                MockHttpSession session = new MockHttpSession();
                session.setAttribute(IS_LOGGED_IN, true);
                session.setAttribute(LOGIN_TIME, System.currentTimeMillis());
                ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
                SessionRequired sessionRequired = mock(SessionRequired.class);

                when(joinPoint.getArgs()).thenReturn(new Object[] { session, Locale.getDefault() });

                try {
                        doReturn(ResponseEntity.ok(new SuccessResponse(MSG_OK)))
                                        .when(joinPoint).proceed();
                } catch (Throwable ignored) {
                }

                ResponseEntity<SessionCheckResponse> response = (ResponseEntity<SessionCheckResponse>) validator
                                .checkSession(joinPoint, sessionRequired);

                assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
                assertTrue(response.getBody() instanceof SuccessResponse);
                assertEquals(MSG_OK, ((SuccessResponse) response.getBody()).getMessage());

                try {
                        verify(joinPoint, times(EXPECTED_PROCEED_INVOCATION_COUNT)).proceed();
                } catch (Throwable ignored) {
                }
        }

        /** session が null の場合、IllegalStateException が送出されることを確認 */
        @Test
        void testMissingHttpSession() throws Exception {
                ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
                SessionRequired sessionRequired = mock(SessionRequired.class);
                Locale locale = Locale.JAPANESE;

                when(joinPoint.getArgs()).thenReturn(new Object[] { locale });
                when(messageSource.getMessage(eq(ERROR_MISSING_HTTP_SESSION), any(), eq(locale)))
                                .thenReturn(MSG_SESSION_NOEXIT);

                IllegalStateException thrown = assertThrows(IllegalStateException.class,
                                () -> validator.checkSession(joinPoint, sessionRequired));

                assertEquals(MSG_SESSION_NOEXIT, thrown.getMessage());

                try {
                        verify(joinPoint, never()).proceed();
                } catch (Throwable ignored) {
                }
        }

        /** isLoggedIn は true だが loginTime が null の場合、401 が返ることを確認 */
        @Test
        void testLoginTimeMissing() throws Exception {
                MockHttpSession session = new MockHttpSession();
                session.setAttribute(IS_LOGGED_IN, true);

                ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
                SessionRequired sessionRequired = mock(SessionRequired.class);

                when(joinPoint.getArgs()).thenReturn(new Object[] { session });
                when(messageSource.getMessage(eq(ERROR_NOT_LOGGED_IN), any(), any(Locale.class)))
                                .thenReturn(MSG_NOT_LOGGED_IN);

                ResponseEntity<SessionCheckResponse> response = validator.checkSession(joinPoint, sessionRequired);

                assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
                assertTrue(response.getBody() instanceof ErrorResponse);
                assertEquals(MSG_NOT_LOGGED_IN, ((ErrorResponse) response.getBody()).getError());

                try {
                        verify(joinPoint, never()).proceed();
                } catch (Throwable ignored) {
                }
        }

        /** joinPoint.proceed() が ResponseEntity 以外を返す場合、SUCCESS_PROCESS が返ることを確認 */
        @Test
        void testProceedReturnsNonResponseEntity() throws Exception {
                MockHttpSession session = new MockHttpSession();
                session.setAttribute(IS_LOGGED_IN, true);
                session.setAttribute(LOGIN_TIME, System.currentTimeMillis());
                ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
                SessionRequired sessionRequired = mock(SessionRequired.class);

                when(joinPoint.getArgs()).thenReturn(new Object[] { session, Locale.getDefault() });

                try {
                        doReturn(MSG_NON_RESPONSE_ENTITY_RESULT).when(joinPoint).proceed();
                } catch (Throwable ignored) {
                }

                when(messageSource.getMessage(eq(SUCCESS_PROCESS), any(), any(Locale.class)))
                                .thenReturn(MSG_SUCCESS_PROCESS);

                ResponseEntity<SessionCheckResponse> response = validator.checkSession(joinPoint, sessionRequired);

                assertEquals(HttpStatus.OK, response.getStatusCode());
                assertTrue(response.getBody() instanceof SuccessResponse);
                assertEquals(MSG_SUCCESS_PROCESS, ((SuccessResponse) response.getBody()).getMessage());

                try {
                        verify(joinPoint, times(EXPECTED_PROCEED_INVOCATION_COUNT)).proceed();
                } catch (Throwable ignored) {
                }
        }

        /** joinPoint.proceed() が例外を投げた場合、Exception が送出されることを確認 */
        @Test
        void testJoinPointProceedThrowsException() throws Exception {
                MockHttpSession session = new MockHttpSession();
                session.setAttribute(IS_LOGGED_IN, true);
                session.setAttribute(LOGIN_TIME, System.currentTimeMillis());
                ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
                SessionRequired sessionRequired = mock(SessionRequired.class);
                Locale locale = Locale.JAPANESE;

                when(joinPoint.getArgs()).thenReturn(new Object[] { session, locale });

                try {
                        doThrow(new RuntimeException(ERROR_PROCEED_JOINPOINT_FAILED)).when(joinPoint).proceed();
                } catch (Throwable ignored) {
                }

                when(messageSource.getMessage(eq(ERROR_PROCEED_JOINPOINT_FAILED), any(), eq(locale)))
                                .thenReturn(ERROR_PROCEED_JOINPOINT_FAILED);

                Exception thrown = assertThrows(Exception.class,
                                () -> validator.checkSession(joinPoint, sessionRequired));

                assertNotNull(thrown.getMessage(), ERROR_EXCEPTION_MESSAGE_NULL);
                assertTrue(thrown.getMessage().contains(ERROR_PROCEED_JOINPOINT_FAILED));
                assertTrue(thrown.getCause() instanceof RuntimeException);

                try {
                        verify(joinPoint, times(EXPECTED_PROCEED_INVOCATION_COUNT)).proceed();
                } catch (Throwable ignored) {
                }
        }

        /** joinPoint.proceed() が例外を投げ、かつ例外メッセージが null の場合の確認 */
        @Test
        void testJoinPointProceedThrowsExceptionWithNullMessage() throws Exception {
                MockHttpSession session = new MockHttpSession();
                session.setAttribute(IS_LOGGED_IN, true);
                session.setAttribute(LOGIN_TIME, System.currentTimeMillis());
                ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
                SessionRequired sessionRequired = mock(SessionRequired.class);
                Locale locale = Locale.JAPANESE;

                when(joinPoint.getArgs()).thenReturn(new Object[] { session, locale });

                RuntimeException ex = new RuntimeException((String) null);

                try {
                        doThrow(ex).when(joinPoint).proceed();
                } catch (Throwable ignored) {
                }

                Exception thrown = assertThrows(Exception.class,
                                () -> validator.checkSession(joinPoint, sessionRequired));

                assertNotNull(thrown.getMessage(), MSG_EXCEPTION_SHOULD_NOT_BE_NULL);
                assertTrue(thrown.getMessage().contains(ERROR_PROCEED_JOINPOINT_FAILED),
                                String.format(MSG_EXCEPTION_SHOULD_CONTAIN, ERROR_PROCEED_JOINPOINT_FAILED,
                                                thrown.getMessage()));
                assertSame(ex, thrown.getCause());

                try {
                        verify(joinPoint, times(EXPECTED_PROCEED_INVOCATION_COUNT)).proceed();
                } catch (Throwable ignored) {
                }
        }

        /**
         * joinPoint.proceed() が ResponseEntity で、かつ body が SessionCheckResponse
         * でない場合の確認
         */
        @Test
        void testProceedReturnsResponseEntityWithNonSessionCheckResponseBody() throws Exception {
                MockHttpSession session = new MockHttpSession();
                session.setAttribute(IS_LOGGED_IN, true);
                session.setAttribute(LOGIN_TIME, System.currentTimeMillis());
                ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
                SessionRequired sessionRequired = mock(SessionRequired.class);
                Locale locale = Locale.getDefault();

                when(joinPoint.getArgs()).thenReturn(new Object[] { session, locale });

                ResponseEntity<String> entity = ResponseEntity.ok(MSG_NON_RESPONSE_SESSION_CHECK);

                try {
                        doReturn(entity).when(joinPoint).proceed();
                } catch (Throwable ignored) {
                }

                when(messageSource.getMessage(eq(SUCCESS_PROCESS), any(), eq(locale)))
                                .thenReturn(MSG_SUCCESS_PROCESS);

                ResponseEntity<SessionCheckResponse> response = validator.checkSession(joinPoint, sessionRequired);

                assertTrue(response.getBody() instanceof SuccessResponse);
                assertEquals(MSG_SUCCESS_PROCESS, ((SuccessResponse) response.getBody()).getMessage());

                try {
                        verify(joinPoint, times(EXPECTED_PROCEED_INVOCATION_COUNT)).proceed();
                } catch (Throwable ignored) {
                }
        }
}