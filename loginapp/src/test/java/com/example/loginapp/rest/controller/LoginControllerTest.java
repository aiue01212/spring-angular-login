package com.example.loginapp.rest.controller;

import com.example.loginapp.LoginappApplication;
import com.example.loginapp.domain.entity.User;
import com.example.loginapp.domain.service.SessionService;
import com.example.loginapp.domain.service.UserService;
import com.example.loginapp.rest.model.LoginRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static com.example.loginapp.constants.SessionKeys.*;

/**
 * {@link LoginController} の動作を検証するテストクラス。
 */
@SpringBootTest(classes = LoginappApplication.class)
@AutoConfigureMockMvc
class LoginControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @MockitoBean
        private UserService userService;

        @MockitoBean
        private SessionService sessionService;

        /** セッション有効期限 */
        private static final long SESSION_TIMEOUT_MILLIS = 60_000L;
        private static final long SESSION_EXPIRED_OFFSET_MILLIS = 5000L;

        /**
         * テストや処理中に使用されるメッセージ定数をまとめた定義。
         */
        private static final String MSG_SUCCESS_LOGIN = "ログインに成功しました";
        private static final String MSG_INVALID_CREDENTIALS = "ユーザIDまたはパスワードが違います";
        private static final String MSG_LOGOUT = "ログアウトしました";
        private static final String MSG_SESSION_EXPIRED = "セッションがタイムアウトしました";
        private static final String MSG_SESSION_ACTIVE = "ログイン中";
        private static final String MSG_NOT_LOGGED_IN = "未ログインです";
        private static final String MSG_INTERNAL_ERROR = "サーバー内部エラーが発生しました";

        /**
         * 正常なログイン処理を確認するテスト。
         */
        @Test
        void loginSuccessTest() throws Exception {
                LoginRequest request = new LoginRequest();
                request.setUsername("user");
                request.setPassword("pass");

                User mockUser = new User();
                mockUser.setUsername("user");
                mockUser.setPassword("pass");

                when(userService.findUser("user")).thenReturn(mockUser);

                mockMvc.perform(post("/api/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                                .header("Accept-Language", "ja")
                                .session(new MockHttpSession()))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.message").value(MSG_SUCCESS_LOGIN));
        }

        /**
         * 認証失敗時のレスポンスを確認するテスト。
         */
        @Test
        void loginFailTest() throws Exception {
                LoginRequest request = new LoginRequest();
                request.setUsername("user");
                request.setPassword("wrong");

                mockMvc.perform(post("/api/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                                .header("Accept-Language", "ja"))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.error").value(MSG_INVALID_CREDENTIALS));
        }

        /**
         * ログアウト処理を確認するテスト。
         */
        @Test
        void logoutTest() throws Exception {
                MockHttpSession session = new MockHttpSession();
                session.setAttribute(IS_LOGGED_IN, true);
                session.setAttribute(LOGIN_TIME, System.currentTimeMillis());

                mockMvc.perform(post("/api/logout")
                                .session(session)
                                .header("Accept-Language", "ja"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.message").value(MSG_LOGOUT));
        }

        /**
         * セッションが期限切れの場合、401が返ることを確認。
         */
        @Test
        void sessionExpiredTest() throws Exception {
                MockHttpSession session = new MockHttpSession();
                session.setAttribute(IS_LOGGED_IN, true);
                session.setAttribute(LOGIN_TIME,
                                System.currentTimeMillis() - SESSION_TIMEOUT_MILLIS - SESSION_EXPIRED_OFFSET_MILLIS);

                mockMvc.perform(get("/api/session-check").session(session)
                                .header("Accept-Language", "ja"))
                                .andExpect(status().isUnauthorized())
                                .andExpect(jsonPath("$.error").value(MSG_SESSION_EXPIRED));
        }

        /**
         * 有効なセッションでアクセスできることを確認。
         */
        @Test
        void sessionActiveTest() throws Exception {
                MockHttpSession session = new MockHttpSession();
                session.setAttribute(IS_LOGGED_IN, true);
                session.setAttribute(LOGIN_TIME, System.currentTimeMillis());

                mockMvc.perform(get("/api/session-check").session(session)
                                .header("Accept-Language", "ja"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.message").value(MSG_SESSION_ACTIVE));
        }

        /** 未ログインでアクセスした場合、401になることを確認 */
        @Test
        void sessionNotLoggedInTest() throws Exception {
                MockHttpSession session = new MockHttpSession();

                mockMvc.perform(get("/api/session-check").session(session)
                                .header("Accept-Language", "ja"))
                                .andExpect(status().isUnauthorized())
                                .andExpect(jsonPath("$.error").value(MSG_NOT_LOGGED_IN));
        }

        /**
         * user == null の場合（ユーザーが存在しない）の分岐をテスト。
         */
        @Test
        void loginUserNotFoundTest() throws Exception {
                LoginRequest request = new LoginRequest();
                request.setUsername("no_user");
                request.setPassword("any");

                when(userService.findUser("no_user")).thenReturn(null);

                mockMvc.perform(post("/api/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                                .header("Accept-Language", "ja"))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.error").value(MSG_INVALID_CREDENTIALS));
        }

        /**
         * ユーザーは存在するがパスワードが一致しない場合の分岐をテスト。
         */
        @Test
        void loginPasswordMismatchTest() throws Exception {
                LoginRequest request = new LoginRequest();
                request.setUsername("user");
                request.setPassword("wrong_password");

                User mockUser = new User();
                mockUser.setUsername("user");
                mockUser.setPassword("correct_password");

                when(userService.findUser("user")).thenReturn(mockUser);

                mockMvc.perform(post("/api/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                                .header("Accept-Language", "ja"))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.error").value(MSG_INVALID_CREDENTIALS));
        }

        @Test
        void loginDataAccessExceptionTest() throws Exception {
                LoginRequest request = new LoginRequest();
                request.setUsername("user");
                request.setPassword("pass");

                when(userService.findUser("user")).thenThrow(new DataAccessException("DB error") {
                });

                mockMvc.perform(post("/api/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                                .header("Accept-Language", "ja")
                                .session(new MockHttpSession()))
                                .andExpect(status().isInternalServerError())
                                .andExpect(jsonPath("$.error").value(MSG_INTERNAL_ERROR));
        }

        @Test
        void loginSessionIllegalStateExceptionTest() throws Exception {
                LoginRequest request = new LoginRequest();
                request.setUsername("user");
                request.setPassword("pass");

                User mockUser = new User();
                mockUser.setUsername("user");
                mockUser.setPassword("pass");

                when(userService.findUser("user")).thenReturn(mockUser);
                doThrow(new IllegalStateException("Session error"))
                                .when(sessionService).createLoginSession(any(), anyString());

                mockMvc.perform(post("/api/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                                .header("Accept-Language", "ja")
                                .session(new MockHttpSession()))
                                .andExpect(status().isInternalServerError())
                                .andExpect(jsonPath("$.error").value(MSG_INTERNAL_ERROR));
        }

        @Test
        void logoutIllegalStateExceptionTest() throws Exception {
                MockHttpSession session = new MockHttpSession();

                doThrow(new IllegalStateException("Session invalidate error"))
                                .when(sessionService).invalidateSession(any());

                mockMvc.perform(post("/api/logout")
                                .session(session)
                                .header("Accept-Language", "ja"))
                                .andExpect(status().isInternalServerError())
                                .andExpect(jsonPath("$.error").value(MSG_INTERNAL_ERROR));
        }
}