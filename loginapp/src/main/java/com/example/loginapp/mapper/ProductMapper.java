package com.example.loginapp.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.example.loginapp.entity.Product;

/**
 * 商品テーブルへアクセスするMyBatisマッパー。
 */
@Mapper
public interface ProductMapper {

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
}
