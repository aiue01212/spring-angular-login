package com.example.loginapp.entity;

import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * 商品情報を表すエンティティクラス。
 */
@Data
@RequiredArgsConstructor
public class Product {

    /** 商品ID */
    private final int id;

    /** 商品名 */
    private final String name;

    /** 商品価格 */
    private final double price;
}