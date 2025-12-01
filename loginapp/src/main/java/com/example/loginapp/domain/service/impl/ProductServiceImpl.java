package com.example.loginapp.domain.service.impl;

import com.example.loginapp.domain.model.Product;
import com.example.loginapp.domain.repository.ProductRepository;
import com.example.loginapp.domain.service.ProductService;

import lombok.RequiredArgsConstructor;

import java.util.Locale;

import static com.example.loginapp.usecase.constants.MessageKeys.ERROR_ROLLBACK_TEST;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * {@link ProductService} の実装クラス。
 * 商品データの取得および更新処理をリポジトリを通じて実行する。
 * トランザクション管理が必要な更新処理もここで行う。
 */
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    /** 商品リポジトリ */
    private final ProductRepository productRepository;

    /** メッセージリソース */
    private final MessageSource messageSource;

    /**
     * すべての商品を取得する。
     *
     * @return 商品リスト
     */
    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    /**
     * 商品ID指定で商品を取得する。
     *
     * @param id 商品ID
     * @return 商品情報、存在しなければ null
     */
    @Override
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
    @Override
    @Transactional
    public void updateTwoProductsWithRollback(int id1, BigDecimal price1, int id2, BigDecimal price2) {

        productRepository.updatePrice(id1, price1);
        productRepository.updatePrice(id2, price2);

        String rollbackMsg = messageSource.getMessage(ERROR_ROLLBACK_TEST, null, Locale.getDefault());
        throw new RuntimeException(rollbackMsg);
    }
}