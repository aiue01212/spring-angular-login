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

import java.util.List;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * {@link ProductController} の動作を検証するテストクラス。
 */
@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductMapper productMapper;

    /**
     * 未ログイン状態で商品一覧を取得しようとした場合、
     * ステータス401が返ることを確認するテスト。
     */
    @Test
    void getProducts_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("未ログインです"));
    }

    /**
     * ログイン済み状態で商品一覧を取得できることを確認するテスト。
     */
    @Test
    void getProducts_LoggedIn() throws Exception {
        List<Product> dummyList = List.of(
                new Product(1, "iPhone", 120000),
                new Product(2, "Galaxy", 98000));
        when(productMapper.findAll()).thenReturn(dummyList);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("isLoggedIn", true);

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

    /**
     * 未ログイン状態で商品詳細を取得しようとした場合、
     * ステータス401が返ることを確認するテスト。
     */
    @Test
    void getProductById_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("未ログインです"));
    }

    /**
     * ログイン済み状態で存在する商品IDを指定した場合、
     * 商品情報が返ることを確認するテスト。
     */
    @Test
    void getProductById_LoggedIn_Found() throws Exception {
        Product dummy = new Product(1, "MacBook", 240000);
        when(productMapper.findById(1)).thenReturn(dummy);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("isLoggedIn", true);

        mockMvc.perform(get("/api/products/1").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("MacBook"))
                .andExpect(jsonPath("$.price").value(240000.0));

        verify(productMapper, times(1)).findById(1);
    }

    /**
     * ログイン済み状態で存在しない商品IDを指定した場合、
     * ステータス404が返ることを確認するテスト。
     */
    @Test
    void getProductById_LoggedIn_NotFound() throws Exception {
        when(productMapper.findById(999)).thenReturn(null);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("isLoggedIn", true);

        mockMvc.perform(get("/api/products/999").session(session))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("指定された商品が見つかりません"));

        verify(productMapper, times(1)).findById(999);
    }
}
