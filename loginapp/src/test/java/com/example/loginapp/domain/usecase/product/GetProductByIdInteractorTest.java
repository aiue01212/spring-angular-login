package com.example.loginapp.domain.usecase.product;

import com.example.loginapp.domain.model.Product;
import com.example.loginapp.domain.service.ProductService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessException;

import java.math.BigDecimal;

import static com.example.loginapp.domain.constants.MessageKeys.ERROR_PRODUCT_NOT_FOUND_ID;
import static com.example.loginapp.domain.usecase.constants.Constants.*;
import static com.example.loginapp.domain.usecase.constants.UseCaseErrorCodes.DB_ERROR;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GetProductByIdInteractorTest {

    /**
     * 商品情報取得用のサービスモック。
     */
    private ProductService productService;

    /**
     * 商品ID指定で取得するユースケースのインタラクター。
     */
    private GetProductByIdInteractor interactor;

    /**
     * 各テスト実行前にサービスモックとインタラクターを初期化する。
     */
    @BeforeEach
    void setUp() {
        productService = mock(ProductService.class);
        interactor = new GetProductByIdInteractor(productService);
    }

    /**
     * iPhone の取得が正常に行えることを検証する。
     */
    @Test
    void testHandleSuccess_iPhone() {
        Product product = new Product(PRODUCT_ID_IPHONE, PRODUCT_NAME_IPHONE, BigDecimal.valueOf(PRICE_IPHONE));
        when(productService.getProductById(PRODUCT_ID_IPHONE)).thenReturn(product);

        GetProductByIdOutputData output = interactor.handle(new GetProductByIdInputData(PRODUCT_ID_IPHONE));

        assertTrue(output.isSuccess());
        assertEquals(product, output.getProduct());
        assertNull(output.getErrorCode());
    }

    /**
     * Galaxy の取得が正常に行えることを検証する。
     */
    @Test
    void testHandleSuccess_Galaxy() {
        Product product = new Product(PRODUCT_ID_GALAXY, PRODUCT_NAME_GALAXY, BigDecimal.valueOf(PRICE_GALAXY));
        when(productService.getProductById(PRODUCT_ID_GALAXY)).thenReturn(product);

        GetProductByIdOutputData output = interactor.handle(new GetProductByIdInputData(PRODUCT_ID_GALAXY));

        assertTrue(output.isSuccess());
        assertEquals(product, output.getProduct());
        assertNull(output.getErrorCode());
    }

    /**
     * 商品が存在しない場合、適切にエラーコードが返されることを検証する。
     */
    @Test
    void testHandleProductNotFound() {
        when(productService.getProductById(PRODUCT_ID_NOT_FOUND)).thenReturn(null);

        GetProductByIdOutputData output = interactor.handle(new GetProductByIdInputData(PRODUCT_ID_NOT_FOUND));

        assertFalse(output.isSuccess());
        assertEquals(ERROR_PRODUCT_NOT_FOUND_ID, output.getErrorCode());
        assertEquals(String.valueOf(PRODUCT_ID_NOT_FOUND), output.getErrorDetail());
    }

    /**
     * データベース例外発生時に適切にエラーが返されることを検証する。
     */
    @Test
    void testHandleDatabaseError() {
        when(productService.getProductById(PRODUCT_ID_IPHONE)).thenThrow(new DataAccessException(DB_ERROR) {
        });

        assertThrows(DataAccessException.class,
                () -> interactor.handle(new GetProductByIdInputData(PRODUCT_ID_IPHONE)));
    }
}