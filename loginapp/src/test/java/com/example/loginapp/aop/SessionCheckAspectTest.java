package com.example.loginapp.aop;

import com.example.loginapp.config.SessionProperties;
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
 * {@link SessionCheckAspect} の動作を検証する単体テスト。
 * <p>
 * 未ログイン・セッション期限切れ・正常ケースの3パターンを検証する。
 * </p>
 */
class SessionCheckAspectTest {

        private SessionCheckAspect aspect;
        private MessageSource messageSource;
        private SessionProperties sessionProperties;

        /** セッション有効期限 */
        private static final long SESSION_TIMEOUT_MILLIS = 60_000L;

        /** タイムアウト検証用：有効期限を超過させる時間（2分 = 120_000ms） */
        private static final long EXPIRED_SESSION_OFFSET_MILLIS = SESSION_TIMEOUT_MILLIS * 2;

        /** joinPoint.proceed() の呼び出し回数（正常系のみ1回） */
        private static final int EXPECTED_PROCEED_INVOCATION_COUNT = 1;

        private static final String MSG_NOT_LOGGED_IN = "ログインしていません";
        private static final String MSG_SESSION_EXPIRED = "セッションがタイムアウトしました";
        private static final String MSG_SUCCESS_PROCESS = "成功しました";
        private static final String MSG_OK = "OK";
        private static final String MSG_SESSION_NOEXIT = "セッションが存在しません";
        private static final String MSG_NON_RESPONSE_ENTITY_RESULT = "非ResponseEntityの結果";

        @BeforeEach
        void setUp() {
                messageSource = mock(MessageSource.class);
                sessionProperties = new SessionProperties();
                sessionProperties.setTimeoutMillis(SESSION_TIMEOUT_MILLIS);
                aspect = new SessionCheckAspect(messageSource, sessionProperties);

                when(messageSource.getMessage(eq(ERROR_NOT_LOGGED_IN), any(), any(Locale.class)))
                                .thenReturn(MSG_NOT_LOGGED_IN);
                when(messageSource.getMessage(eq(ERROR_SESSION_EXPIRED), any(), any(Locale.class)))
                                .thenReturn(MSG_SESSION_EXPIRED);
                when(messageSource.getMessage(eq(SUCCESS_PROCESS), any(), any(Locale.class)))
                                .thenReturn(MSG_SUCCESS_PROCESS);
        }

        /** 未ログインの場合、401エラーが返ることを確認 */
        @Test
        void testNotLoggedIn() throws Throwable {
                MockHttpSession session = new MockHttpSession();
                ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
                SessionRequired sessionRequired = mock(SessionRequired.class);

                when(joinPoint.getArgs()).thenReturn(new Object[] { session });

                ResponseEntity<SessionCheckResponse> response = (ResponseEntity<SessionCheckResponse>) aspect
                                .checkSession(joinPoint, sessionRequired);

                assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatusCode().value());
                assertTrue(response.getBody() instanceof ErrorResponse);
                assertEquals(MSG_NOT_LOGGED_IN, ((ErrorResponse) response.getBody()).getError());

                verify(joinPoint, never()).proceed();
        }

        /** セッション期限切れの場合、401エラーが返ることを確認 */
        @Test
        void testSessionExpired() throws Throwable {
                MockHttpSession session = new MockHttpSession();
                session.setAttribute(IS_LOGGED_IN, true);
                session.setAttribute(LOGIN_TIME, System.currentTimeMillis() - EXPIRED_SESSION_OFFSET_MILLIS);
                ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
                SessionRequired sessionRequired = mock(SessionRequired.class);

                when(joinPoint.getArgs()).thenReturn(new Object[] { session });

                ResponseEntity<SessionCheckResponse> response = (ResponseEntity<SessionCheckResponse>) aspect
                                .checkSession(joinPoint, sessionRequired);

                assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatusCode().value());
                assertTrue(response.getBody() instanceof ErrorResponse);
                assertEquals(MSG_SESSION_EXPIRED,
                                ((ErrorResponse) response.getBody()).getError());

                verify(joinPoint, never()).proceed();
        }

        /** 有効なセッションの場合、joinPoint.proceed() の結果が返ることを確認 */
        @Test
        void testValidSession() throws Throwable {
                MockHttpSession session = new MockHttpSession();
                session.setAttribute(IS_LOGGED_IN, true);
                session.setAttribute(LOGIN_TIME, System.currentTimeMillis());
                ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
                SessionRequired sessionRequired = mock(SessionRequired.class);

                when(joinPoint.getArgs()).thenReturn(new Object[] { session, Locale.getDefault() });

                when(joinPoint.proceed()).thenReturn(ResponseEntity.ok(new SuccessResponse(MSG_OK)));

                ResponseEntity<SessionCheckResponse> response = (ResponseEntity<SessionCheckResponse>) aspect
                                .checkSession(joinPoint, sessionRequired);

                assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
                assertTrue(response.getBody() instanceof SuccessResponse);
                assertEquals(MSG_OK, ((SuccessResponse) response.getBody()).getMessage());

                verify(joinPoint, times(EXPECTED_PROCEED_INVOCATION_COUNT)).proceed();
        }

        /** session が null の場合、IllegalStateException が送出されることを確認 */
        @Test
        void testMissingHttpSession() throws Throwable {
                ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
                SessionRequired sessionRequired = mock(SessionRequired.class);
                Locale locale = Locale.JAPANESE;

                when(joinPoint.getArgs()).thenReturn(new Object[] { locale });
                when(messageSource.getMessage(eq(ERROR_MISSING_HTTP_SESSION), any(), eq(locale)))
                                .thenReturn(MSG_SESSION_NOEXIT);

                IllegalStateException thrown = assertThrows(IllegalStateException.class,
                                () -> aspect.checkSession(joinPoint, sessionRequired));

                assertEquals(MSG_SESSION_NOEXIT, thrown.getMessage());
                verify(joinPoint, never()).proceed();
        }

        /** isLoggedIn は true だが loginTime が null の場合、401 が返ることを確認 */
        @Test
        void testLoginTimeMissing() throws Throwable {
                MockHttpSession session = new MockHttpSession();
                session.setAttribute(IS_LOGGED_IN, true);

                ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
                SessionRequired sessionRequired = mock(SessionRequired.class);

                when(joinPoint.getArgs()).thenReturn(new Object[] { session });
                when(messageSource.getMessage(eq(ERROR_NOT_LOGGED_IN), any(), any(Locale.class)))
                                .thenReturn(MSG_NOT_LOGGED_IN);

                ResponseEntity<SessionCheckResponse> response = aspect.checkSession(joinPoint, sessionRequired);

                assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
                assertTrue(response.getBody() instanceof ErrorResponse);
                assertEquals(MSG_NOT_LOGGED_IN, ((ErrorResponse) response.getBody()).getError());
                verify(joinPoint, never()).proceed();
        }

        /** joinPoint.proceed() が ResponseEntity 以外を返す場合、SUCCESS_PROCESS が返ることを確認 */
        @Test
        void testProceedReturnsNonResponseEntity() throws Throwable {
                MockHttpSession session = new MockHttpSession();
                session.setAttribute(IS_LOGGED_IN, true);
                session.setAttribute(LOGIN_TIME, System.currentTimeMillis());
                ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
                SessionRequired sessionRequired = mock(SessionRequired.class);

                when(joinPoint.getArgs()).thenReturn(new Object[] { session, Locale.getDefault() });
                when(joinPoint.proceed()).thenReturn(MSG_NON_RESPONSE_ENTITY_RESULT);
                when(messageSource.getMessage(eq(SUCCESS_PROCESS), any(), any(Locale.class)))
                                .thenReturn(MSG_SUCCESS_PROCESS);

                ResponseEntity<SessionCheckResponse> response = aspect.checkSession(joinPoint, sessionRequired);

                assertEquals(HttpStatus.OK, response.getStatusCode());
                assertTrue(response.getBody() instanceof SuccessResponse);
                assertEquals(MSG_SUCCESS_PROCESS, ((SuccessResponse) response.getBody()).getMessage());

                verify(joinPoint, times(EXPECTED_PROCEED_INVOCATION_COUNT)).proceed();
        }
}