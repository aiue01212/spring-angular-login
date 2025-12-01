package com.example.loginapp.domain.model;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 商品情報を表すエンティティクラス。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    /** 商品ID */
    private int id;

    /** 商品名 */
    private String name;

    /** 商品価格 */
    // private double price;
    private BigDecimal price;
}