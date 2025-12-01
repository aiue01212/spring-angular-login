package com.example.loginapp.domain.repository;

import java.math.BigDecimal;
import java.util.List;

import com.example.loginapp.domain.model.Product;

/**
 * 商品エンティティに対する永続化操作を定義するリポジトリインターフェース。
 */
public interface ProductRepository {

    /**
     * 全商品の一覧を取得する。
     *
     * @return 商品リスト
     */
    List<Product> findAll();

    /**
     * 商品IDを指定して商品を取得する。
     *
     * @param id 商品ID
     * @return 該当する商品、存在しない場合はnull
     */
    Product findById(int id);

    /**
     * 商品の価格を更新する。
     *
     * @param id    更新対象の商品ID
     * @param price 新しい価格
     */
    void updatePrice(int id, BigDecimal price);

    /**
     * 商品操作に関するログを記録する。
     *
     * @param productId 商品ID
     * @param action    実施アクション
     */
    void insertProductLog(int productId, String action);
}