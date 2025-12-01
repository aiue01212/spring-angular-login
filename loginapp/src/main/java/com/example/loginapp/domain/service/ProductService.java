package com.example.loginapp.domain.service;

import com.example.loginapp.domain.model.Product;

import java.math.BigDecimal;
import java.util.List;

/**
 * 商品情報に関するドメインサービスのインターフェース。
 * リポジトリを利用して商品データの取得・更新などを行う。
 */
public interface ProductService {

    /**
     * すべての商品を取得する。
     *
     * @return 商品リスト
     */
    List<Product> getAllProducts();

    /**
     * 商品IDを指定して商品を取得する。
     *
     * @param id 商品ID
     * @return 商品情報。存在しない場合は null
     */
    Product getProductById(int id);

    /**
     * 2つの商品価格を更新し、最後に例外を発生させることで
     * トランザクションのロールバックが有効であることを確認するためのテスト処理。
     *
     * @param id1    商品1のID
     * @param price1 商品1の新価格
     * @param id2    商品2のID
     * @param price2 商品2の新価格
     * @throws RuntimeException ロールバック確認のため常に発生
     */
    void updateTwoProductsWithRollback(int id1, BigDecimal price1, int id2, BigDecimal price2);
}