package com.example.loginapp.usecase.product;

import com.example.loginapp.domain.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessException;

import static com.example.loginapp.usecase.constants.Constants.*;
import static com.example.loginapp.usecase.constants.UseCaseErrorCodes.DB_ERROR;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UpdateTwoProductsInteractorTest {

    /**
     * 商品更新用サービスのモック。
     */
    private ProductService productService;

    /**
     * 2商品同時更新ユースケースのインタラクター。
     */
    private UpdateTwoProductsInteractor interactor;

    /**
     * 各テスト実行前にサービスモックとインタラクターを初期化する。
     */
    @BeforeEach
    void setUp() {
        productService = mock(ProductService.class);
        interactor = new UpdateTwoProductsInteractor(productService);
    }

    /**
     * 2商品同時更新が正常に行えることを検証する。
     */
    @Test
    void testHandleSuccess() {
        UpdateTwoProductsInputData input = new UpdateTwoProductsInputData(
                PRODUCT_ID_IPHONE, PRICE_UPDATE_1,
                PRODUCT_ID_GALAXY, PRICE_UPDATE_2);

        doNothing().when(productService).updateTwoProductsWithRollback(
                PRODUCT_ID_IPHONE, PRICE_UPDATE_1,
                PRODUCT_ID_GALAXY, PRICE_UPDATE_2);

        UpdateTwoProductsOutputData output = interactor.handle(input);

        assertTrue(output.isSuccess());
        assertNull(output.getErrorMessage());
    }

    /**
     * データベース例外発生時に適切にエラーが返されることを検証する。
     */
    @Test
    void testHandleDatabaseError() {
        UpdateTwoProductsInputData input = new UpdateTwoProductsInputData(
                PRODUCT_ID_IPHONE, PRICE_UPDATE_1,
                PRODUCT_ID_GALAXY, PRICE_UPDATE_2);

        doThrow(new DataAccessException(DB_ERROR) {
        })
                .when(productService)
                .updateTwoProductsWithRollback(
                        PRODUCT_ID_IPHONE, PRICE_UPDATE_1,
                        PRODUCT_ID_GALAXY, PRICE_UPDATE_2);

        UpdateTwoProductsOutputData output = interactor.handle(input);

        assertFalse(output.isSuccess());
        assertNotNull(output.getErrorMessage());
    }
}
