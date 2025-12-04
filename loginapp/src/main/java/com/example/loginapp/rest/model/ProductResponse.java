package com.example.loginapp.rest.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

import com.example.loginapp.domain.model.Product;

/**
 * 商品情報返却用のレスポンス DTO。
 */
@Data
@AllArgsConstructor
public class ProductResponse implements SessionCheckResponse {
    private List<Product> products;
}