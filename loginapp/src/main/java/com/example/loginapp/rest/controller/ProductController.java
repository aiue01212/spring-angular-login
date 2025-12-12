package com.example.loginapp.rest.controller;

import com.example.loginapp.domain.usecase.product.GetAllProductsInputBoundary;
import com.example.loginapp.domain.usecase.product.GetAllProductsInputData;
import com.example.loginapp.domain.usecase.product.GetAllProductsOutputData;
import com.example.loginapp.domain.usecase.product.GetProductByIdInputBoundary;
import com.example.loginapp.domain.usecase.product.GetProductByIdInputData;
import com.example.loginapp.domain.usecase.product.GetProductByIdOutputData;
import com.example.loginapp.domain.usecase.product.UpdateTwoProductsInputBoundary;
import com.example.loginapp.domain.usecase.product.UpdateTwoProductsInputData;
import com.example.loginapp.domain.usecase.product.UpdateTwoProductsOutputData;
import com.example.loginapp.rest.annotation.SessionRequired;
import com.example.loginapp.rest.model.ErrorResponse;
import com.example.loginapp.rest.model.ProductResponse;
import com.example.loginapp.rest.model.SessionCheckResponse;

import jakarta.servlet.http.HttpSession;
import org.springframework.context.MessageSource;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.example.loginapp.domain.constants.MessageKeys.*;
import static com.example.loginapp.rest.constants.UpdateConstants.*;

import java.util.List;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 商品情報を管理するコントローラークラス。
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    /** 全商品取得用 UseCase */
    private final GetAllProductsInputBoundary getAllProductsUseCase;

    /** ID指定商品取得用 UseCase */
    private final GetProductByIdInputBoundary getProductByIdUseCase;

    /** 更新テスト用 UseCase */
    private final UpdateTwoProductsInputBoundary updateProductsUseCase;

    /** メッセージリソース */
    private final MessageSource messageSource;

    /**
     * ログ出力用のLogger
     */
    private static final Logger log = LoggerFactory.getLogger(ProductController.class);

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

        GetAllProductsOutputData outputData = getAllProductsUseCase.handle(new GetAllProductsInputData());

        ProductResponse response = new ProductResponse(outputData.getProducts());

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

        GetProductByIdOutputData outputData = getProductByIdUseCase.handle(new GetProductByIdInputData(id));

        if (outputData.getProduct() == null) {
            String msg = messageSource.getMessage(ERROR_PRODUCT_NOT_FOUND, null, locale);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(msg));
        }

        ProductResponse response = new ProductResponse(List.of(outputData.getProduct()));

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

        UpdateTwoProductsInputData input = new UpdateTwoProductsInputData(
                PRODUCT_ID_1,
                PRICE_UPDATE_1,
                PRODUCT_ID_2,
                PRICE_UPDATE_2);

        UpdateTwoProductsOutputData outputData = updateProductsUseCase.handle(input);

        if (!outputData.isSuccess()) {
            String errorMsg = messageSource.getMessage(ERROR_ROLLBACK_OCCURRED,
                    new Object[] { outputData.getErrorMessage() }, locale);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(errorMsg));
        }

        String successMsg = messageSource.getMessage(SUCCESS_UPDATE_WITH_ROLLBACK, null, locale);

        log.info("API終了: /products/update-test");
        return ResponseEntity.ok(new ErrorResponse(successMsg));
    }
}