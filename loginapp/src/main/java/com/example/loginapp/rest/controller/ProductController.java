package com.example.loginapp.rest.controller;

import com.example.loginapp.annotation.SessionRequired;
import com.example.loginapp.domain.entity.Product;
import com.example.loginapp.domain.service.ProductService;
import com.example.loginapp.rest.model.ErrorResponse;
import com.example.loginapp.rest.model.ProductResponse;
import com.example.loginapp.rest.model.SessionCheckResponse;

import jakarta.servlet.http.HttpSession;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataAccessException;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import static com.example.loginapp.constants.MessageKeys.*;

import java.util.List;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 商品情報を管理するコントローラークラス。
 */
@CrossOrigin(origins = "http://localhost:9090", allowCredentials = "true")
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    /** 商品サービス */
    private final ProductService productService;

    /** メッセージリソース */
    private final MessageSource messageSource;

    /**
     * ログ出力用のLogger
     */
    private static final Logger log = LoggerFactory.getLogger(ProductController.class);

    /** 処理中に使用されるメッセージ定数をまとめた定義。 */
    private static final int PRODUCT_ID_1 = 1;
    private static final int PRODUCT_ID_2 = 2;
    private static final double PRICE_PRODUCT_1 = 130000.0;
    private static final double PRICE_PRODUCT_2 = 99000.0;

    /**
     * 商品一覧を取得する。
     *
     * @param session HttpSession
     * @param locale  ロケール情報
     * @return ログインしていれば商品一覧、未ログインならエラー
     */
    @GetMapping
    @SessionRequired
    public ResponseEntity<SessionCheckResponse> getProducts(HttpSession session, Locale locale) {
        log.info("API開始: /products");
        List<Product> products;
        try {
            products = productService.getAllProducts();
        } catch (DataAccessException e) {
            log.error("商品一覧取得中に例外発生", e);
            String msg = messageSource.getMessage(ERROR_DATABASE_ACCESS, null, locale);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(msg));
        }

        ProductResponse response = new ProductResponse(products);
        log.info("商品一覧取得件数: {}", products.size());
        log.info("API終了: /products");
        return ResponseEntity.ok(response);
    }

    /**
     * 商品ID指定で商品情報を取得する。
     *
     * @param id      商品ID
     * @param session HttpSession
     * @param locale  ロケール情報
     * @return 商品情報またはエラーメッセージ
     */
    @GetMapping("/{id}")
    @SessionRequired
    public ResponseEntity<SessionCheckResponse> getProductById(@PathVariable int id, HttpSession session,
            Locale locale) {
        log.info("API開始: /products/{}", id);
        Product product;
        try {
            product = productService.getProductById(id);
        } catch (DataAccessException e) {
            log.error("商品取得中に例外発生: ID={}", id, e);
            String msg = messageSource.getMessage(ERROR_DATABASE_ACCESS, null, locale);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(msg));
        }

        if (product == null) {
            String errorMsg = messageSource.getMessage(ERROR_PRODUCT_NOT_FOUND, null, locale);
            log.warn("商品未取得: ID={}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(errorMsg));
        }

        ProductResponse response = new ProductResponse(List.of(product));
        log.info("商品取得成功: ID={}", id);
        log.info("API終了: /products/{}", id);
        return ResponseEntity.ok(response);
    }

    /**
     * rollback 確認用エンドポイント
     */
    @PostMapping("/update-test")
    @SessionRequired
    public ResponseEntity<ErrorResponse> updateTest(HttpSession session, Locale locale) {
        log.info("API開始: /products/update-test");
        try {
            productService.updateTwoProductsWithRollback(PRODUCT_ID_1, PRICE_PRODUCT_1, PRODUCT_ID_2, PRICE_PRODUCT_2);
        } catch (DataAccessException e) {
            log.error("商品更新中にデータベース例外発生", e);
            String dbErrorMsg = messageSource.getMessage(ERROR_DATABASE_ACCESS, new Object[] { e.getMessage() },
                    locale);
            String errorMsg = messageSource.getMessage(ERROR_ROLLBACK_OCCURRED, new Object[] { dbErrorMsg }, locale);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(errorMsg));
        } catch (RuntimeException e) {
            log.error("商品更新中に例外発生", e);
            String errorMsg = messageSource.getMessage(ERROR_ROLLBACK_OCCURRED,
                    new Object[] { e.getMessage() }, locale);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(errorMsg));
        }

        String successMsg = messageSource.getMessage(SUCCESS_UPDATE_WITH_ROLLBACK, null, locale);
        log.info("商品更新成功: rollbackテスト完了");
        log.info("API終了: /products/update-test");
        return ResponseEntity.ok(new ErrorResponse(successMsg));
    }
}