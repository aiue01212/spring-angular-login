package com.example.loginapp.domain.usecase.product;

/**
 * 商品ID指定取得処理の入力境界（InputBoundary）。
 */
public interface GetProductByIdInputBoundary {

    /**
     * 商品IDを指定して取得処理を実行する。
     *
     * @param input 商品ID
     * @return 取得結果を含む出力データ
     */
    GetProductByIdOutputData handle(GetProductByIdInputData input);
}
