package com.example.loginapp.domain.repository;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.example.loginapp.domain.entity.Product;

/**
 * 商品テーブルへアクセスするMyBatisマッパー。
 */
@Mapper
public interface ProductRepository {

    /**
     * 全商品の一覧を取得する。
     *
     * @return 商品リスト
     */
    @Select("SELECT id, name, price FROM products")
    List<Product> findAll();

    /**
     * 商品IDを指定して商品を取得する。
     *
     * @param id 商品ID
     * @return 該当する商品、存在しない場合はnull
     */
    @Select("SELECT id, name, price FROM products WHERE id = #{id}")
    Product findById(int id);

    /**
     * 商品価格を更新する。
     *
     * @param id    商品ID
     * @param price 新しい価格
     */
    @Update("UPDATE products SET price = #{price} WHERE id = #{id}")
    void updatePrice(@Param("id") int id, @Param("price") double price);

    /**
     * ログテーブルに記録する（rollback確認用）。
     * まだログテーブルが無ければ作成する必要があります。
     *
     * @param productId 商品ID
     * @param action    実施アクション
     */
    @Insert("INSERT INTO product_logs (product_id, action) VALUES (#{productId}, #{action})")
    void insertProductLog(@Param("productId") int productId, @Param("action") String action);
}
