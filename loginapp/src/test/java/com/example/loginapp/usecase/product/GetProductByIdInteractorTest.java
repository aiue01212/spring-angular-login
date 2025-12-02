package com.example.loginapp.usecase.product;

import com.example.loginapp.domain.model.Product;
import com.example.loginapp.domain.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessException;

import java.math.BigDecimal;

import static com.example.loginapp.usecase.constants.Constants.*;
import static com.example.loginapp.usecase.constants.UseCaseErrorCodes.DB_ERROR;
import static com.example.loginapp.usecase.constants.MessageKeys.ERROR_PRODUCT_NOT_FOUND_ID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GetProductByIdInteractorTest {

    private ProductService productService;
    private GetProductByIdInteractor interactor;

    @BeforeEach
    void setUp() {
        productService = mock(ProductService.class);
        interactor = new GetProductByIdInteractor(productService);
    }

    @Test
    void testHandleSuccess_iPhone() {
        Product product = new Product(PRODUCT_ID_IPHONE, PRODUCT_NAME_IPHONE, BigDecimal.valueOf(PRICE_IPHONE));
        when(productService.getProductById(PRODUCT_ID_IPHONE)).thenReturn(product);

        GetProductByIdOutputData output = interactor.handle(new GetProductByIdInputData(PRODUCT_ID_IPHONE));

        assertTrue(output.isSuccess());
        assertEquals(product, output.getProduct());
        assertNull(output.getErrorCode());
    }

    @Test
    void testHandleSuccess_Galaxy() {
        Product product = new Product(PRODUCT_ID_GALAXY, PRODUCT_NAME_GALAXY, BigDecimal.valueOf(PRICE_GALAXY));
        when(productService.getProductById(PRODUCT_ID_GALAXY)).thenReturn(product);

        GetProductByIdOutputData output = interactor.handle(new GetProductByIdInputData(PRODUCT_ID_GALAXY));

        assertTrue(output.isSuccess());
        assertEquals(product, output.getProduct());
        assertNull(output.getErrorCode());
    }

    @Test
    void testHandleProductNotFound() {
        when(productService.getProductById(PRODUCT_ID_NOT_FOUND)).thenReturn(null);

        GetProductByIdOutputData output = interactor.handle(new GetProductByIdInputData(PRODUCT_ID_NOT_FOUND));

        assertFalse(output.isSuccess());
        assertEquals(ERROR_PRODUCT_NOT_FOUND_ID, output.getErrorCode());
        assertEquals(String.valueOf(PRODUCT_ID_NOT_FOUND), output.getErrorDetail());
    }

    @Test
    void testHandleDatabaseError() {
        when(productService.getProductById(PRODUCT_ID_IPHONE)).thenThrow(new DataAccessException(DB_ERROR) {
        });

        GetProductByIdOutputData output = interactor.handle(new GetProductByIdInputData(PRODUCT_ID_IPHONE));

        assertFalse(output.isSuccess());
        assertEquals(DB_ERROR, output.getErrorCode());
        assertNotNull(output.getErrorDetail());
    }
}