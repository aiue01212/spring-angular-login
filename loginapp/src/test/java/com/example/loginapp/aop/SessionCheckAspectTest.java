package com.example.loginapp.aop;

import com.example.loginapp.config.SessionProperties;
import com.example.loginapp.dto.ErrorResponse;
import com.example.loginapp.dto.SessionCheckResponse;
import com.example.loginapp.dto.SuccessResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpSession;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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

        // メッセージコード定数
        private static final String CODE_NOT_LOGGED_IN = "error.notLoggedIn";
        private static final String CODE_SESSION_EXPIRED = "error.sessionExpired";
        private static final String CODE_SUCCESS_PROCESS = "success.process";

        // メッセージ文字列定数
        private static final String MSG_NOT_LOGGED_IN = "ログインしていません";
        private static final String MSG_SESSION_EXPIRED = "セッションがタイムアウトしました";
        private static final String MSG_SUCCESS_PROCESS = "成功しました";

        @BeforeEach
        void setUp() {
                messageSource = mock(MessageSource.class);
                sessionProperties = new SessionProperties();
                sessionProperties.setTimeoutMillis(SESSION_TIMEOUT_MILLIS);
                aspect = new SessionCheckAspect(messageSource, sessionProperties);

                // 共通メッセージモックを定数で設定
                when(messageSource.getMessage(eq(CODE_NOT_LOGGED_IN), any(), any(Locale.class)))
                                .thenReturn(MSG_NOT_LOGGED_IN);
                when(messageSource.getMessage(eq(CODE_SESSION_EXPIRED), any(), any(Locale.class)))
                                .thenReturn(MSG_SESSION_EXPIRED);
                when(messageSource.getMessage(eq(CODE_SUCCESS_PROCESS), any(), any(Locale.class)))
                                .thenReturn(MSG_SUCCESS_PROCESS);
        }

        /** 未ログインの場合、401エラーが返ることを確認 */
        @Test
        void testNotLoggedIn() throws Throwable {
                MockHttpSession session = new MockHttpSession();
                ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
                SessionRequired sessionRequired = mock(SessionRequired.class);

                // joinPoint に HttpSession を引数として設定
                when(joinPoint.getArgs()).thenReturn(new Object[] { session });

                // @SuppressWarnings("unchecked")
                ResponseEntity<SessionCheckResponse> response = (ResponseEntity<SessionCheckResponse>) aspect
                                .checkSession(joinPoint, sessionRequired);

                assertEquals(401, response.getStatusCode().value());
                assertTrue(response.getBody() instanceof ErrorResponse);
                assertEquals("ログインしていません", ((ErrorResponse) response.getBody()).getError());

                verify(joinPoint, never()).proceed();
        }

        /** セッション期限切れの場合、401エラーが返ることを確認 */
        @Test
        void testSessionExpired() throws Throwable {
                MockHttpSession session = new MockHttpSession();
                session.setAttribute("isLoggedIn", true);
                session.setAttribute("loginTime", System.currentTimeMillis() - 120_000); // 2分前
                ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
                SessionRequired sessionRequired = mock(SessionRequired.class);

                // joinPoint に HttpSession を引数として設定
                when(joinPoint.getArgs()).thenReturn(new Object[] { session });

                ResponseEntity<SessionCheckResponse> response = (ResponseEntity<SessionCheckResponse>) aspect
                                .checkSession(joinPoint, sessionRequired);

                assertEquals(401, response.getStatusCode().value());
                assertTrue(response.getBody() instanceof ErrorResponse);
                assertEquals("セッションがタイムアウトしました",
                                ((ErrorResponse) response.getBody()).getError());

                verify(joinPoint, never()).proceed();
        }

        /** 有効なセッションの場合、joinPoint.proceed() の結果が返ることを確認 */
        @Test
        void testValidSession() throws Throwable {
                MockHttpSession session = new MockHttpSession();
                session.setAttribute("isLoggedIn", true);
                session.setAttribute("loginTime", System.currentTimeMillis());
                ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
                SessionRequired sessionRequired = mock(SessionRequired.class);

                // joinPoint に HttpSession と Locale を設定
                when(joinPoint.getArgs()).thenReturn(new Object[] { session, Locale.getDefault() });

                // proceed() の戻り値をモック
                when(joinPoint.proceed()).thenReturn(ResponseEntity.ok(new SuccessResponse("OK")));

                ResponseEntity<SessionCheckResponse> response = (ResponseEntity<SessionCheckResponse>) aspect
                                .checkSession(joinPoint, sessionRequired);

                assertEquals(200, response.getStatusCode().value());
                assertTrue(response.getBody() instanceof SuccessResponse);
                assertEquals("OK", ((SuccessResponse) response.getBody()).getMessage());

                verify(joinPoint, times(1)).proceed();
        }
}