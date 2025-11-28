package com.example.loginapp.usecase.product;

import com.example.loginapp.domain.model.Product;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * 全商品取得処理の結果 DTO。
 */
@Data
@AllArgsConstructor
public class GetAllProductsOutputData {

    /** 成功フラグ */
    private final boolean success;

    /** 取得した商品リスト（成功時） */
    private final List<Product> products;

    /** エラーコード（失敗時） */
    private final String errorCode;

    /** エラー詳細（必要に応じて） */
    private final String errorDetail;
}
