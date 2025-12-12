package com.example.loginapp.domain.usecase.product;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 商品IDによる商品取得の入力データ DTO。
 */
@Data
@AllArgsConstructor
public class GetProductByIdInputData {

    /** 取得対象の商品ID */
    private final int productId;
}