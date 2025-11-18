package com.example.loginapp.rest.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataAccessException;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.loginapp.LoginappApplication;
import com.example.loginapp.domain.entity.Product;
import com.example.loginapp.domain.service.ProductService;

import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static com.example.loginapp.domain.constants.MessageKeys.*;
import static com.example.loginapp.domain.constants.SessionKeys.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * {@link ProductController} の AOP (@SessionRequired) 対応テスト。
 */
@SpringBootTest(classes = LoginappApplication.class)
@AutoConfigureMockMvc
class ProductControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private ProductService productService;

        @MockitoBean
        private MessageSource messageSource;

        /** セッション有効期限 */
        private static final long SESSION_TIMEOUT_MILLIS = 60_000L;
        private static final long SESSION_EXPIRED_OFFSET_MILLIS = 1_000L;

        /**
         * テストや処理中に使用されるメッセージ定数をまとめた定義。
         */
        private static final String MSG_NOT_LOGGED_IN = "未ログインです";
        private static final String MSG_SESSION_EXPIRED = "セッションがタイムアウトしました";
        private static final String MSG_SUCCESS_PROCESS = "処理成功";
        private static final String MSG_PRODUCT_NOT_FOUND = "商品が見つかりません";
        private static final String MSG_ROLLBACK = "ロールバックされました";
        private static final String MSG_UPDATE_WITH_ROLLBACK = "更新成功（ただし例外でロールバックされます）";
        private static final String MSG_ROLLBACK_OCCURRED = "更新中に例外が発生し、ロールバックされました: テスト用例外";
        private static final String MSG_TEST_EXCEPTION = "テスト用例外";
        private static final String MSG_DB_CONNECTION_FAILED = "DB接続失敗";

        private static final int PRODUCT_ID_IPHONE = 1;
        private static final int PRODUCT_ID_GALAXY = 2;
        private static final int PRODUCT_ID_NOT_FOUND = 999;

        private static final double PRICE_IPHONE = 120000.0;
        private static final double PRICE_GALAXY = 98000.0;
        private static final double PRICE_UPDATED_IPHONE = 130000.0;
        private static final double PRICE_UPDATED_GALAXY = 99000.0;

        private static final int EXPECTED_CALL_ONCE = 1;

        @BeforeEach
        void setUp() {
                when(messageSource.getMessage(eq(ERROR_NOT_LOGGED_IN), any(), any(Locale.class)))
                                .thenReturn(MSG_NOT_LOGGED_IN);
                when(messageSource.getMessage(eq(ERROR_SESSION_EXPIRED), any(), any(Locale.class)))
                                .thenReturn(MSG_SESSION_EXPIRED);
                when(messageSource.getMessage(eq(SUCCESS_PROCESS), any(), any(Locale.class)))
                                .thenReturn(MSG_SUCCESS_PROCESS);
                when(messageSource.getMessage(eq(ERROR_PRODUCT_NOT_FOUND), any(), any(Locale.class)))
                                .thenReturn(MSG_PRODUCT_NOT_FOUND);
                when(messageSource.getMessage(eq(SUCCESS_UPDATE_WITH_ROLLBACK), any(), any(Locale.class)))
                                .thenReturn(MSG_UPDATE_WITH_ROLLBACK);
                when(messageSource.getMessage(eq(ERROR_ROLLBACK_OCCURRED), any(), any(Locale.class)))
                                .thenReturn(MSG_ROLLBACK_OCCURRED);
        }

        /** 未ログイン状態で /api/products にアクセスすると 401 が返ることを確認 */
        @Test
        void getProducts_Unauthorized() throws Exception {
                mockMvc.perform(get("/api/products"))
                                .andExpect(status().isUnauthorized())
                                .andExpect(jsonPath("$.error").value(MSG_NOT_LOGGED_IN));

                verify(productService, never()).getAllProducts();
        }

        /** セッション期限切れの場合 401 が返ることを確認 */
        @Test
        void getProducts_SessionExpired() throws Exception {
                MockHttpSession session = new MockHttpSession();
                session.setAttribute(IS_LOGGED_IN, true);
                session.setAttribute(LOGIN_TIME,
                                System.currentTimeMillis() - SESSION_TIMEOUT_MILLIS - SESSION_EXPIRED_OFFSET_MILLIS);

                mockMvc.perform(get("/api/products").session(session))
                                .andExpect(status().isUnauthorized())
                                .andExpect(jsonPath("$.error").value(MSG_SESSION_EXPIRED));

                verify(productService, never()).getAllProducts();
        }

        /** ログイン済みで商品一覧を取得できることを確認 */
        @Test
        void getProducts_LoggedIn() throws Exception {
                List<Product> dummyList = Arrays.asList(
                                new Product(PRODUCT_ID_IPHONE, "iPhone", PRICE_IPHONE),
                                new Product(PRODUCT_ID_GALAXY, "Galaxy", PRICE_GALAXY));
                when(productService.getAllProducts()).thenReturn(dummyList);

                MockHttpSession session = new MockHttpSession();
                session.setAttribute(IS_LOGGED_IN, true);
                session.setAttribute(LOGIN_TIME, System.currentTimeMillis());

                mockMvc.perform(get("/api/products").session(session))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.products[0].id").value(PRODUCT_ID_IPHONE))
                                .andExpect(jsonPath("$.products[0].name").value("iPhone"))
                                .andExpect(jsonPath("$.products[0].price").value(PRICE_IPHONE))
                                .andExpect(jsonPath("$.products[1].id").value(PRODUCT_ID_GALAXY))
                                .andExpect(jsonPath("$.products[1].name").value("Galaxy"))
                                .andExpect(jsonPath("$.products[1].price").value(PRICE_GALAXY));

                verify(productService, times(EXPECTED_CALL_ONCE)).getAllProducts();
        }

        /** getProducts() で DataAccessException が発生した場合 500 が返ることを確認 */
        @Test
        void getProducts_DataAccessException() throws Exception {
                doThrow(new DataAccessException(MSG_DB_CONNECTION_FAILED) {
                }).when(productService).getAllProducts();

                when(messageSource.getMessage(eq(ERROR_DATABASE_ACCESS), any(), any(Locale.class)))
                                .thenReturn(MSG_DB_CONNECTION_FAILED);

                MockHttpSession session = new MockHttpSession();
                session.setAttribute(IS_LOGGED_IN, true);
                session.setAttribute(LOGIN_TIME, System.currentTimeMillis());

                mockMvc.perform(get("/api/products").session(session))
                                .andExpect(status().isInternalServerError())
                                .andExpect(jsonPath("$.error").value(MSG_DB_CONNECTION_FAILED));

                verify(productService, times(EXPECTED_CALL_ONCE)).getAllProducts();
        }

        /** 未ログインで /api/products/{id} にアクセスすると 401 */
        @Test
        void getProductById_Unauthorized() throws Exception {
                mockMvc.perform(get("/api/products/" + PRODUCT_ID_IPHONE))
                                .andExpect(status().isUnauthorized())
                                .andExpect(jsonPath("$.error").value(MSG_NOT_LOGGED_IN));

                verify(productService, never()).getProductById(anyInt());
        }

        /** セッション期限切れで /api/products/{id} にアクセスすると 401 */
        @Test
        void getProductById_SessionExpired() throws Exception {
                MockHttpSession session = new MockHttpSession();
                session.setAttribute(IS_LOGGED_IN, true);
                session.setAttribute(LOGIN_TIME,
                                System.currentTimeMillis() - SESSION_TIMEOUT_MILLIS - SESSION_EXPIRED_OFFSET_MILLIS);

                mockMvc.perform(get("/api/products/" + PRODUCT_ID_IPHONE).session(session))
                                .andExpect(status().isUnauthorized())
                                .andExpect(jsonPath("$.error").value(MSG_SESSION_EXPIRED));

                verify(productService, never()).getProductById(anyInt());
        }

        /** ログイン済みで存在する商品を取得できることを確認 */
        @Test
        void getProductById_LoggedIn_Found() throws Exception {
                Product product = new Product(PRODUCT_ID_IPHONE, "iPhone", PRICE_IPHONE);
                when(productService.getProductById(PRODUCT_ID_IPHONE)).thenReturn(product);

                MockHttpSession session = new MockHttpSession();
                session.setAttribute(IS_LOGGED_IN, true);
                session.setAttribute(LOGIN_TIME, System.currentTimeMillis());

                mockMvc.perform(get("/api/products/" + PRODUCT_ID_IPHONE).session(session))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.products[0].id").value(PRODUCT_ID_IPHONE))
                                .andExpect(jsonPath("$.products[0].name").value("iPhone"))
                                .andExpect(jsonPath("$.products[0].price").value(PRICE_IPHONE));

                verify(productService, times(EXPECTED_CALL_ONCE)).getProductById(PRODUCT_ID_IPHONE);
        }

        /** getProductById() で DataAccessException が発生した場合 500 が返ることを確認 */
        @Test
        void getProductById_DataAccessException() throws Exception {
                doThrow(new DataAccessException(MSG_DB_CONNECTION_FAILED) {
                }).when(productService).getProductById(PRODUCT_ID_IPHONE);

                when(messageSource.getMessage(eq(ERROR_DATABASE_ACCESS), any(), any(Locale.class)))
                                .thenReturn(MSG_DB_CONNECTION_FAILED);

                MockHttpSession session = new MockHttpSession();
                session.setAttribute(IS_LOGGED_IN, true);
                session.setAttribute(LOGIN_TIME, System.currentTimeMillis());

                mockMvc.perform(get("/api/products/" + PRODUCT_ID_IPHONE).session(session))
                                .andExpect(status().isInternalServerError())
                                .andExpect(jsonPath("$.error").value(MSG_DB_CONNECTION_FAILED));

                verify(productService, times(EXPECTED_CALL_ONCE)).getProductById(PRODUCT_ID_IPHONE);
        }

        /** ログイン済みだが商品が存在しない場合 404 */
        @Test
        void getProductById_LoggedIn_NotFound() throws Exception {
                when(productService.getProductById(PRODUCT_ID_NOT_FOUND)).thenReturn(null);

                MockHttpSession session = new MockHttpSession();
                session.setAttribute(IS_LOGGED_IN, true);
                session.setAttribute(LOGIN_TIME, System.currentTimeMillis());

                mockMvc.perform(get("/api/products/999").session(session))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.error").value(MSG_PRODUCT_NOT_FOUND));

                verify(productService, times(EXPECTED_CALL_ONCE)).getProductById(PRODUCT_ID_NOT_FOUND);
        }

        /** rollback エンドポイントのテスト */
        @Test
        void updateTest_Rollback() throws Exception {
                doThrow(new RuntimeException(MSG_TEST_EXCEPTION)).when(productService)
                                .updateTwoProductsWithRollback(PRODUCT_ID_IPHONE, PRICE_UPDATED_IPHONE,
                                                PRODUCT_ID_GALAXY, PRICE_UPDATED_GALAXY);

                MockHttpSession session = new MockHttpSession();
                session.setAttribute(IS_LOGGED_IN, true);
                session.setAttribute(LOGIN_TIME, System.currentTimeMillis());

                mockMvc.perform(post("/api/products/update-test")
                                .session(session)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isInternalServerError())
                                .andExpect(jsonPath("$.error", org.hamcrest.Matchers.containsString(MSG_ROLLBACK)));

                verify(productService, times(EXPECTED_CALL_ONCE))
                                .updateTwoProductsWithRollback(PRODUCT_ID_IPHONE, PRICE_UPDATED_IPHONE,
                                                PRODUCT_ID_GALAXY, PRICE_UPDATED_GALAXY);
        }

        /** rollback エンドポイントの成功パターン（例外なし）をテスト */
        @Test
        void updateTest_Success() throws Exception {
                doNothing().when(productService)
                                .updateTwoProductsWithRollback(anyInt(), anyDouble(), anyInt(), anyDouble());

                when(messageSource.getMessage(eq(SUCCESS_UPDATE_WITH_ROLLBACK), any(), any(Locale.class)))
                                .thenReturn(MSG_UPDATE_WITH_ROLLBACK);

                MockHttpSession session = new MockHttpSession();
                session.setAttribute(IS_LOGGED_IN, true);
                session.setAttribute(LOGIN_TIME, System.currentTimeMillis());

                mockMvc.perform(post("/api/products/update-test")
                                .session(session)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.error").value(MSG_UPDATE_WITH_ROLLBACK));

                verify(productService, times(EXPECTED_CALL_ONCE))
                                .updateTwoProductsWithRollback(anyInt(), anyDouble(), anyInt(), anyDouble());
        }

        /**
         * ProductController の rollback 確認用エンドポイント
         * "/api/products/update-test" に対して DataAccessException が発生した場合の
         * 動作を確認するテスト。
         */
        @Test
        void updateTest_DataAccessException() throws Exception {
                doThrow(new DataAccessException(MSG_DB_CONNECTION_FAILED) {
                }).when(productService)
                                .updateTwoProductsWithRollback(PRODUCT_ID_IPHONE, PRICE_UPDATED_IPHONE,
                                                PRODUCT_ID_GALAXY, PRICE_UPDATED_GALAXY);

                String dbErrorMsg = messageSource.getMessage(ERROR_DATABASE_ACCESS,
                                new Object[] { MSG_DB_CONNECTION_FAILED },
                                Locale.getDefault());
                String expectedErrorMsg = messageSource.getMessage(ERROR_ROLLBACK_OCCURRED, new Object[] { dbErrorMsg },
                                Locale.getDefault());

                MockHttpSession session = new MockHttpSession();
                session.setAttribute(IS_LOGGED_IN, true);
                session.setAttribute(LOGIN_TIME, System.currentTimeMillis());

                mockMvc.perform(post("/api/products/update-test")
                                .session(session)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isInternalServerError())
                                .andExpect(jsonPath("$.error").value(expectedErrorMsg));

                verify(productService, times(EXPECTED_CALL_ONCE))
                                .updateTwoProductsWithRollback(PRODUCT_ID_IPHONE, PRICE_UPDATED_IPHONE,
                                                PRODUCT_ID_GALAXY, PRICE_UPDATED_GALAXY);
        }
}