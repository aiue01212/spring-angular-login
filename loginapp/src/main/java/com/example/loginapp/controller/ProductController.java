package com.example.loginapp.controller;

import com.example.loginapp.dto.ProductResponse;
import com.example.loginapp.annotation.SessionRequired;
import com.example.loginapp.dto.ErrorResponse;
import com.example.loginapp.dto.SessionCheckResponse;
import com.example.loginapp.entity.Product;
import jakarta.servlet.http.HttpSession;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataAccessException;

import lombok.RequiredArgsConstructor;
import com.example.loginapp.service.ProductService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import static com.example.loginapp.constants.MessageKeys.*;

import java.util.List;
import java.util.Locale;

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
        List<Product> products = productService.getAllProducts();
        ProductResponse response = new ProductResponse(products);
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
        Product product = productService.getProductById(id);
        if (product != null) {
            ProductResponse response = new ProductResponse(List.of(product));
            return ResponseEntity.ok(response);
        }

        String errorMsg = messageSource.getMessage(ERROR_PRODUCT_NOT_FOUND, null, locale);
        ErrorResponse errorResponse = new ErrorResponse(errorMsg);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    /**
     * rollback 確認用エンドポイント
     */
    @PostMapping("/update-test")
    @SessionRequired
    public ResponseEntity<ErrorResponse> updateTest(HttpSession session, Locale locale) {
        try {
            productService.updateTwoProductsWithRollback(PRODUCT_ID_1, PRICE_PRODUCT_1, PRODUCT_ID_2, PRICE_PRODUCT_2);
        } catch (DataAccessException e) {
            String dbErrorMsg = messageSource.getMessage(ERROR_DATABASE_ACCESS, new Object[] { e.getMessage() },
                    locale);
            String errorMsg = messageSource.getMessage(ERROR_ROLLBACK_OCCURRED, new Object[] { dbErrorMsg }, locale);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(errorMsg));
        } catch (RuntimeException e) {
            String errorMsg = messageSource.getMessage(ERROR_ROLLBACK_OCCURRED,
                    new Object[] { e.getMessage() }, locale);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(errorMsg));
        }

        String successMsg = messageSource.getMessage(SUCCESS_UPDATE_WITH_ROLLBACK, null, locale);
        return ResponseEntity.ok(new ErrorResponse(successMsg));
    }
}