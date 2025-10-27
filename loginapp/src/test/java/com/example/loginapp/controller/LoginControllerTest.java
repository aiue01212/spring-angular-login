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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

        /**
         * 正常なログイン処理を確認するテスト。
         */
        @Test
        void loginSuccessTest() throws Exception {
                LoginRequest request = new LoginRequest();
                request.setUsername("user");
                request.setPassword("pass");

                mockMvc.perform(
                                post("/api/login")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(request))
                                                .session(new MockHttpSession()))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.message").value("ログインに成功しました"));
        }

        /**
         * 認証失敗時のレスポンスを確認するテスト。
         */
        @Test
        void loginFailTest() throws Exception {
                LoginRequest request = new LoginRequest();
                request.setUsername("user");
                request.setPassword("wrong");

                mockMvc.perform(
                                post("/api/login")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.error").value("ユーザIDまたはパスワードが違います"));
        }

        /**
         * 意図的なサーバーエラー発生時のレスポンスを確認するテスト。
         */
        @Test
        void loginServerErrorTest() throws Exception {
                LoginRequest request = new LoginRequest();
                request.setUsername("error");
                request.setPassword("pass");

                mockMvc.perform(
                                post("/api/login")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isInternalServerError());
        }

        /**
         * ログアウト処理を確認するテスト。
         */
        @Test
        void logoutTest() throws Exception {
                mockMvc.perform(post("/api/logout")
                                .session(new MockHttpSession()))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.message").value("ログアウトしました"));
        }

        /**
         * セッション未ログイン時の動作を確認するテスト。
         */
        @Test
        void sessionCheck_Unauthorized() throws Exception {
                mockMvc.perform(get("/api/session-check"))
                                .andExpect(status().isUnauthorized())
                                .andExpect(jsonPath("$.error").value("未ログインです"));
        }

        /**
         * セッションログイン中の動作を確認するテスト。
         */
        @Test
        void sessionCheck_LoggedIn() throws Exception {
                MockHttpSession session = new MockHttpSession();
                session.setAttribute("isLoggedIn", true);
                session.setAttribute("loginTime", System.currentTimeMillis());

                mockMvc.perform(get("/api/session-check")
                                .session(session))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.message").value("ログイン中"));
        }
}