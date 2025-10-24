package com.example.loginapp.controller;

import com.example.loginapp.dto.LoginRequest;
import com.example.loginapp.entity.Product;
import com.example.loginapp.mapper.ProductMapper;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class LoginControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper; // JSON変換用

        @MockitoBean
        private ProductMapper productMapper; // ← モック化

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
                                MockMvcRequestBuilders.get("/api/session-check"))
                                .andExpect(status().isUnauthorized())
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

        // 未ログイン時の /api/products テスト
        @Test
        void getProducts_Unauthorized() throws Exception {
                mockMvc.perform(MockMvcRequestBuilders.get("/api/products"))
                                .andExpect(status().isUnauthorized())
                                .andExpect(jsonPath("$.error").value("未ログインです"));
        }

        // ログイン済み時の /api/products テスト
        @Test
        void getProducts_LoggedIn() throws Exception {
                List<Product> dummyList = List.of(
                                new Product(1, "iPhone", 120000),
                                new Product(2, "Galaxy", 98000));
                when(productMapper.findAll()).thenReturn(dummyList);

                MockHttpSession session = new MockHttpSession();
                session.setAttribute("isLoggedIn", true);
                session.setAttribute("username", "user");
                session.setAttribute("loginTime", System.currentTimeMillis());

                mockMvc.perform(MockMvcRequestBuilders.get("/api/products")
                                .session(session))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].name").value("iPhone"))
                                .andExpect(jsonPath("$[0].price").value(120000))
                                .andExpect(jsonPath("$[1].name").value("Galaxy"))
                                .andExpect(jsonPath("$[1].price").value(98000));

                // 当然モックが呼ばれていることも確認可
                verify(productMapper, times(1)).findAll();
        }
}