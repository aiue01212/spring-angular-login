package com.example.loginapp.usecase.product;

/**
 * 全商品取得処理の入力境界（InputBoundary）。
 * Controller はこのインターフェースに依存することで、
 * Interactor（実装）に依存しなくなる。
 */
public interface GetAllProductsInputBoundary {

    /**
     * 商品全件取得処理を実行する。
     *
     * @param input 特に条件のない入力データ
     * @return 全商品リストを含む出力データ
     */
    GetAllProductsOutputData handle(GetAllProductsInputData input);
}