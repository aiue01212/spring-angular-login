package com.example.loginapp.rest.controller;

import com.example.loginapp.LoginappApplication;
import com.example.loginapp.rest.model.LoginRequest;
import com.example.loginapp.rest.service.SessionService;
import com.example.loginapp.usecase.login.LoginInputBoundary;
import com.example.loginapp.usecase.login.LoginInputData;
import com.example.loginapp.usecase.login.LoginOutputData;
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

import static com.example.loginapp.rest.constants.SessionKeys.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static com.example.loginapp.usecase.constants.UseCaseErrorCodes.*;

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
        private LoginInputBoundary loginUseCase;

        @MockitoBean
        private SessionService sessionService;

        /**
         * テストや処理中に使用されるメッセージ定数をまとめた定義。
         */
        private static final String MSG_SUCCESS_LOGIN = "ログインに成功しました";
        private static final String MSG_INVALID_CREDENTIALS = "ユーザIDまたはパスワードが違います";
        private static final String MSG_LOGOUT = "ログアウトしました";
        private static final String MSG_SESSION_ACTIVE = "ログイン中";
        private static final String MSG_NOT_LOGGED_IN = "未ログインです";
        private static final String MSG_INTERNAL_ERROR = "サーバー内部エラーが発生しました";
        private static final String MSG_SESSION_CREATION_ERROR = "セッション作成エラー";
        public static final String SESSION_INVALIDATE_ERROR = "セッション無効化エラー";

        private static final int EXPECTED_CALL_ONCE = 1;

        /**
         * 正常なログイン処理を確認するテスト。
         */
        @Test
        void loginSuccessTest() throws Exception {
                LoginRequest request = new LoginRequest();
                request.setUsername("user");
                request.setPassword("pass");

                when(loginUseCase.login(any(LoginInputData.class)))
                                .thenReturn(new LoginOutputData(true, "user", null));

                mockMvc.perform(post("/api/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                                .header("Accept-Language", "ja")
                                .session(new MockHttpSession()))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.message").value(MSG_SUCCESS_LOGIN));

                verify(sessionService, times(EXPECTED_CALL_ONCE)).createLoginSession(any(), eq("user"));
        }

        /**
         * ログアウト処理
         */
        @Test
        void logoutTest() throws Exception {
                MockHttpSession session = new MockHttpSession();
                session.setAttribute(IS_LOGGED_IN, true);
                session.setAttribute(LOGIN_TIME, System.currentTimeMillis());

                mockMvc.perform(post("/api/logout").session(session))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.message").value(MSG_LOGOUT));
        }

        /**
         * 有効なセッションでアクセスできること
         */
        @Test
        void sessionActiveTest() throws Exception {
                MockHttpSession session = new MockHttpSession();
                session.setAttribute(IS_LOGGED_IN, true);
                session.setAttribute(LOGIN_TIME, System.currentTimeMillis());

                mockMvc.perform(get("/api/session-check").session(session))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.message").value(MSG_SESSION_ACTIVE));
        }

        /**
         * 未ログイン
         */
        @Test
        void sessionNotLoggedInTest() throws Exception {
                MockHttpSession session = new MockHttpSession();

                mockMvc.perform(get("/api/session-check").session(session))
                                .andExpect(status().isUnauthorized())
                                .andExpect(jsonPath("$.error").value(MSG_NOT_LOGGED_IN));
        }

        /**
         * DataAccessException 発生時のログインテスト
         */
        @Test
        void loginDataAccessExceptionTest() throws Exception {
                LoginRequest request = new LoginRequest();
                request.setUsername("user");
                request.setPassword("pass");

                when(loginUseCase.login(any())).thenThrow(new DataAccessException(DB_ERROR) {
                });

                mockMvc.perform(post("/api/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isInternalServerError())
                                .andExpect(jsonPath("$.error").value(MSG_INTERNAL_ERROR));
        }

        /**
         * セッション作成で IllegalStateException が発生した場合
         */
        @Test
        void loginSessionIllegalStateExceptionTest() throws Exception {
                LoginRequest request = new LoginRequest();
                request.setUsername("user");
                request.setPassword("pass");

                when(loginUseCase.login(any())).thenReturn(new LoginOutputData(true, "user", null));
                doThrow(new IllegalStateException(MSG_SESSION_CREATION_ERROR))
                                .when(sessionService).createLoginSession(any(), anyString());

                mockMvc.perform(post("/api/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isInternalServerError())
                                .andExpect(jsonPath("$.error").value(MSG_INTERNAL_ERROR));
        }

        /**
         * セッション無効化で IllegalStateException が発生した場合
         */
        @Test
        void logoutIllegalStateExceptionTest() throws Exception {
                MockHttpSession session = new MockHttpSession();

                doThrow(new IllegalStateException(SESSION_INVALIDATE_ERROR))
                                .when(sessionService).invalidateSession(any());

                mockMvc.perform(post("/api/logout").session(session))
                                .andExpect(status().isInternalServerError())
                                .andExpect(jsonPath("$.error").value(MSG_INTERNAL_ERROR));
        }

        /**
         * LoginOutputData の errorCode が DB_ERROR の場合
         */
        @Test
        void loginOutputDbErrorTest() throws Exception {
                LoginRequest request = new LoginRequest();
                request.setUsername("user");
                request.setPassword("pass");

                LoginOutputData outputData = new LoginOutputData(false, null, DB_ERROR);
                when(loginUseCase.login(any())).thenReturn(outputData);

                mockMvc.perform(post("/api/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isInternalServerError())
                                .andExpect(jsonPath("$.error").value(MSG_INTERNAL_ERROR));
        }

        /**
         * LoginOutputData の errorCode が null（default ケース）の場合
         */
        @Test
        void loginOutputDefaultErrorTest() throws Exception {
                LoginRequest request = new LoginRequest();
                request.setUsername("user");
                request.setPassword("pass");

                LoginOutputData outputData = new LoginOutputData(false, null, null);
                when(loginUseCase.login(any())).thenReturn(outputData);

                mockMvc.perform(post("/api/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isInternalServerError())
                                .andExpect(jsonPath("$.error").value(MSG_INTERNAL_ERROR));
        }

        /**
         * LoginOutputData の errorCode が INVALID_CREDENTIALS の場合
         */
        @Test
        void loginOutputInvalidCredentialsTest() throws Exception {
                LoginRequest request = new LoginRequest();
                request.setUsername("user");
                request.setPassword("wrong");

                LoginOutputData outputData = new LoginOutputData(false, null, INVALID_CREDENTIALS);
                when(loginUseCase.login(any())).thenReturn(outputData);

                mockMvc.perform(post("/api/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                                .header("Accept-Language", "ja"))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.error").value(MSG_INVALID_CREDENTIALS));
        }
}