package com.example.loginapp.controller;

import com.example.loginapp.dto.LoginRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(LoginController.class)
class LoginControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper; // JSON変換用

        // 正常ログインテスト
        @Test
        void loginSuccessTest() throws Exception {
                LoginRequest request = new LoginRequest();
                request.setUsername("user");
                request.setPassword("pass");

                mockMvc.perform(
                                MockMvcRequestBuilders.post("/api/login")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(request))
                                                .session(new MockHttpSession()))
                                .andExpect(status().isOk());
        }

        // 認証失敗テスト
        @Test
        void loginFailTest() throws Exception {
                LoginRequest request = new LoginRequest();
                request.setUsername("user");
                request.setPassword("wrong");

                mockMvc.perform(
                                MockMvcRequestBuilders.post("/api/login")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.error").value("ユーザIDまたはパスワードが違います"));
        }

        // サーバエラー発生テスト
        @Test
        void loginServerErrorTest() throws Exception {
                LoginRequest request = new LoginRequest();
                request.setUsername("error"); // この値で例外が起きるようにしてる
                request.setPassword("pass");

                mockMvc.perform(
                                MockMvcRequestBuilders.post("/api/login")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isInternalServerError())
                                .andExpect(jsonPath("$.error").value("サーバー内部エラーが発生しました"));
        }

        // ログアウトテスト
        @Test
        void logoutTest() throws Exception {
                mockMvc.perform(
                                MockMvcRequestBuilders.post("/api/logout")
                                                .session(new MockHttpSession()))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.message").value("ログアウトしました"));
        }

        // セッションチェック：未ログイン時
        @Test
        void sessionCheck_Unauthorized() throws Exception {
                mockMvc.perform(
                                MockMvcRequestBuilders.get("/api/session-check")).andExpect(status().isUnauthorized())
                                .andExpect(jsonPath("$.error").value("未ログインです"));
        }

        // セッションチェック：ログイン中
        @Test
        void sessionCheck_LoggedIn() throws Exception {
                MockHttpSession session = new MockHttpSession();
                session.setAttribute("isLoggedIn", true);

                mockMvc.perform(
                                MockMvcRequestBuilders.get("/api/session-check")
                                                .session(session))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.message").value("ログイン中"));
        }
}