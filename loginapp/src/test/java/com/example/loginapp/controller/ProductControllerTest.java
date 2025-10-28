package com.example.loginapp.controller;

import com.example.loginapp.entity.Product;
import com.example.loginapp.mapper.ProductMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * {@link ProductController} の AOP (@SessionRequired) 動作を検証するテスト。
 */
@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerAopTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductMapper productMapper;

    /** セッション有効期限（Aspectと同じ） */
    private static final long SESSION_TIMEOUT_MILLIS = 60_000L;

    /**
     * 未ログイン状態で /api/products にアクセスすると 401 が返ることを確認。
     */
    @Test
    void getProducts_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("未ログインです"));

        verify(productMapper, times(0)).findAll();
    }

    /**
     * セッション有効期限切れの場合に 401（Unauthorized）が返されることを確認するテスト。
     *
     * @throws Exception MockMvc 実行時の例外
     */
    @Test
    void getProducts_SessionExpired() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("isLoggedIn", true);
        session.setAttribute("loginTime", System.currentTimeMillis() - SESSION_TIMEOUT_MILLIS - 1000L);

        mockMvc.perform(get("/api/products").session(session))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("セッション切れです"));

        verify(productMapper, never()).findAll();
    }

    /**
     * ログイン済み状態で /api/products にアクセスできることを確認。
     */
    @Test
    void getProducts_LoggedIn() throws Exception {
        List<Product> dummyList = Arrays.asList(
                new Product(1, "iPhone", 120000.0),
                new Product(2, "Galaxy", 98000.0));
        when(productMapper.findAll()).thenReturn(dummyList);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("isLoggedIn", true);
        session.setAttribute("loginTime", System.currentTimeMillis());

        mockMvc.perform(get("/api/products").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("iPhone"))
                .andExpect(jsonPath("$[0].price").value(120000.0))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Galaxy"))
                .andExpect(jsonPath("$[1].price").value(98000.0));

        verify(productMapper, times(1)).findAll();
    }
}
