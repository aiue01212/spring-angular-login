package com.example.loginapp.usecase.product;

import com.example.loginapp.domain.model.Product;
import com.example.loginapp.domain.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessException;

import java.util.Arrays;
import java.util.List;

import static com.example.loginapp.usecase.constants.Constants.*;
import static com.example.loginapp.usecase.constants.UseCaseErrorCodes.DB_ERROR;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GetAllProductsInteractorTest {

    /**
     * 商品情報を取得するドメインサービスのモック。
     */
    private ProductService productService;

    /**
     * 全商品取得ユースケースのインタラクター。
     */
    private GetAllProductsInteractor interactor;

    /**
     * テスト用の商品リスト。
     */
    private List<Product> dummyList;

    /**
     * 各テスト実行前にサービスのモックとインタラクターを初期化する。
     */
    @BeforeEach
    void setUp() {
        productService = mock(ProductService.class);
        interactor = new GetAllProductsInteractor(productService);

        dummyList = Arrays.asList(
                new Product(PRODUCT_ID_IPHONE, PRODUCT_NAME_IPHONE, PRICE_IPHONE_BD),
                new Product(PRODUCT_ID_GALAXY, PRODUCT_NAME_GALAXY, PRICE_GALAXY_BD));
    }

    /**
     * 全商品の取得が正常に行えることを検証する。
     */
    @Test
    void testHandleSuccess() {
        when(productService.getAllProducts()).thenReturn(dummyList);

        GetAllProductsOutputData output = interactor.handle(new GetAllProductsInputData());

        assertTrue(output.isSuccess());
        assertEquals(TOTAL_PRODUCTS, output.getProducts().size());
        assertEquals(PRODUCT_NAME_IPHONE, output.getProducts().get(INDEX_IPHONE).getName());
        assertEquals(PRICE_GALAXY_BD, output.getProducts().get(INDEX_GALAXY).getPrice());
        assertNull(output.getErrorCode());
    }

    /**
     * データベース例外発生時に適切にエラーが返されることを検証する。
     */
    @Test
    void testHandleDatabaseError() {
        when(productService.getAllProducts()).thenThrow(new DataAccessException(DB_ERROR) {
        });

        GetAllProductsOutputData output = interactor.handle(new GetAllProductsInputData());

        assertFalse(output.isSuccess());
        assertEquals(DB_ERROR, output.getErrorCode());
        assertNotNull(output.getErrorDetail());
    }
}
