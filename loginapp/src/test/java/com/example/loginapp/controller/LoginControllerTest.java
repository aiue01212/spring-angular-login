package com.example.loginapp.controller;

import com.example.loginapp.dto.LoginRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static com.example.loginapp.constants.SessionKeys.*;

/**
 * {@link LoginController} の動作を検証するテストクラス。
 */
@SpringBootTest
@AutoConfigureMockMvc
class LoginControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        /** セッション有効期限 */
        private static final long SESSION_TIMEOUT_MILLIS = 60_000L;

        private static final long SESSION_EXPIRED_OFFSET_MILLIS = 5000L;

        private static final String MSG_SUCCESS_LOGIN = "ログインに成功しました";
        private static final String MSG_INVALID_CREDENTIALS = "ユーザIDまたはパスワードが違います";
        private static final String MSG_LOGOUT = "ログアウトしました";
        private static final String MSG_SESSION_EXPIRED = "セッションがタイムアウトしました";
        private static final String MSG_SESSION_ACTIVE = "ログイン中";
        private static final String MSG_NOT_LOGGED_IN = "未ログインです";

        /**
         * 正常なログイン処理を確認するテスト。
         */
        @Test
        void loginSuccessTest() throws Exception {
                LoginRequest request = new LoginRequest();
                request.setUsername("user");
                request.setPassword("pass");

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
}