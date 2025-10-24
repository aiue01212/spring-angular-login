package com.example.loginapp.entity;

import lombok.Data;

@Data
public class Product {
    private int id;
    private String name;
    private double price;

    // 引数付きコンストラクタを追加
    public Product(int id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }
}