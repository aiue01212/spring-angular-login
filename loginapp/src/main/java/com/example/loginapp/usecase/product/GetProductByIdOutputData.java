package com.example.loginapp.usecase.product;

import com.example.loginapp.domain.model.Product;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 商品ID指定取得処理の結果 DTO。
 * 成功した場合は product に商品情報が格納される。
 */
@Data
@AllArgsConstructor
public class GetProductByIdOutputData {

    /** 成功かどうか */
    private final boolean success;

    /** 取得した商品（存在しない場合は null） */
    private final Product product;

    /** エラーコード（成功時は null） */
    private final String errorCode;

    /** エラー詳細（任意、成功時は null） */
    private final String errorDetail;
}
