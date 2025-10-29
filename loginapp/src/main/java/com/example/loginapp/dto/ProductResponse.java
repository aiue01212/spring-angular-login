package com.example.loginapp.dto;

import com.example.loginapp.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * 商品取得レスポンス（成功時のみ使用）
 */
@Data
@AllArgsConstructor
public class ProductResponse implements SessionCheckResponse {
    private List<Product> products;
}