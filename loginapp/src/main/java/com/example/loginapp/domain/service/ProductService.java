package com.example.loginapp.domain.service;

import com.example.loginapp.domain.entity.Product;
import com.example.loginapp.domain.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

import static com.example.loginapp.domain.constants.MessageKeys.*;

import java.util.List;
import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {

    /** 商品データアクセス */
    private final ProductRepository productRepository;

    /** メッセージリソース */
    private final MessageSource messageSource;

    /**
     * すべての商品を取得する。
     *
     * @return 商品リスト
     */
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    /**
     * 商品ID指定で商品を取得する。
     *
     * @param id 商品ID
     * @return 商品情報、存在しなければ null
     */
    public Product getProductById(int id) {
        return productRepository.findById(id);
    }

    /**
     * 2つの商品の価格を更新するメソッド。
     * 2つ目の更新で例外を発生させ、トランザクションのロールバックを確認する。
     *
     * @param id1    商品1のID
     * @param price1 商品1の新価格
     * @param id2    商品2のID
     * @param price2 商品2の新価格
     */
    @Transactional
    public void updateTwoProductsWithRollback(int id1, double price1, int id2, double price2) {

        productRepository.updatePrice(id1, price1);
        productRepository.updatePrice(id2, price2);

        String rollbackMsg = messageSource.getMessage(ERROR_ROLLBACK_TEST, null, Locale.getDefault());
        throw new RuntimeException(rollbackMsg);
    }
}