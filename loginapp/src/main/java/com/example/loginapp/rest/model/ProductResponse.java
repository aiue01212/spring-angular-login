package com.example.loginapp.rest.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

import com.example.loginapp.domain.entity.Product;

/**
 * 商品取得レスポンス（成功時のみ使用）
 */
@Data
@AllArgsConstructor
public class ProductResponse implements SessionCheckResponse {
    private List<Product> products;
}