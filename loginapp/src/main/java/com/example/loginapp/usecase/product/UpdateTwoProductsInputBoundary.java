package com.example.loginapp.usecase.product;

/**
 * 商品2件の更新（ロールバック確認用）を実行する UseCase の入力境界。
 */
public interface UpdateTwoProductsInputBoundary {

    /**
     * 2件の商品を更新し、テスト的に例外を発生させる。
     *
     * @param input 更新対象商品IDと価格
     * @return 結果を含む出力データ
     */
    UpdateTwoProductsOutputData handle(UpdateTwoProductsInputData input);
}
