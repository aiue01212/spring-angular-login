package com.example.loginapp.usecase;

import com.example.loginapp.domain.model.Product;
import com.example.loginapp.usecase.constants.UseCaseErrorCodes;
import com.example.loginapp.usecase.login.LoginInputBoundary;
import com.example.loginapp.usecase.login.LoginInputData;
import com.example.loginapp.usecase.login.LoginOutputData;
import com.example.loginapp.usecase.product.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static com.example.loginapp.usecase.constants.UseCaseErrorCodes.*;
import static com.example.loginapp.usecase.constants.Constants.*;
import static com.example.loginapp.usecase.constants.MessageKeys.ERROR_PRODUCT_NOT_FOUND_ID;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UseCaseIntegrationTest {

    @Autowired
    private LoginInputBoundary loginInteractor;

    @Autowired
    private GetAllProductsInputBoundary getAllProductsInteractor;

    @Autowired
    private GetProductByIdInputBoundary getProductByIdInteractor;

    @Autowired
    private UpdateTwoProductsInputBoundary updateTwoProductsInteractor;

    // ----------------------------
    // LoginInteractor 統合テスト
    // ----------------------------
    @Test
    void testLoginSuccess() {
        LoginInputData input = new LoginInputData(VALID_USERNAME, VALID_PASSWORD);
        LoginOutputData output = loginInteractor.login(input);

        assertTrue(output.isSuccess());
        assertEquals(VALID_USERNAME, output.getUsername());
        assertNull(output.getErrorCode());
    }

    @Test
    void testLoginInvalidPassword() {
        LoginInputData input = new LoginInputData(VALID_USERNAME, INVALID_PASSWORD);
        LoginOutputData output = loginInteractor.login(input);

        assertFalse(output.isSuccess());
        assertEquals(INVALID_CREDENTIALS, output.getErrorCode());
    }

    // ----------------------------
    // GetAllProductsInteractor 統合テスト
    // ----------------------------
    @Test
    void testGetAllProducts() {
        GetAllProductsOutputData output = getAllProductsInteractor.handle(new GetAllProductsInputData());

        assertTrue(output.isSuccess());
        assertNotNull(output.getProducts());
        assertFalse(output.getProducts().isEmpty());

        Product iphone = output.getProducts().stream()
                .filter(p -> PRODUCT_NAME_IPHONE.equals(p.getName()))
                .findFirst()
                .orElse(null);

        assertNotNull(iphone);
        assertEquals(BIGDECIMAL_EQUAL, iphone.getPrice().compareTo(PRICE_IPHONE_BD));

        Product galaxy = output.getProducts().stream()
                .filter(p -> PRODUCT_NAME_GALAXY.equals(p.getName()))
                .findFirst()
                .orElse(null);

        assertNotNull(galaxy);
        assertEquals(BIGDECIMAL_EQUAL, galaxy.getPrice().compareTo(PRICE_GALAXY_BD));
    }

    // ----------------------------
    // GetProductByIdInteractor 統合テスト
    // ----------------------------
    @Test
    void testGetProductByIdSuccess() {
        GetProductByIdOutputData output = getProductByIdInteractor
                .handle(new GetProductByIdInputData(PRODUCT_ID_IPHONE));

        assertTrue(output.isSuccess());
        assertNotNull(output.getProduct());
        assertEquals(PRODUCT_ID_IPHONE, output.getProduct().getId());
    }

    @Test
    void testGetProductByIdNotFound() {
        GetProductByIdOutputData output = getProductByIdInteractor
                .handle(new GetProductByIdInputData(PRODUCT_ID_NOT_FOUND));

        assertFalse(output.isSuccess());
        assertEquals(ERROR_PRODUCT_NOT_FOUND_ID, output.getErrorCode());
    }

    // ----------------------------
    // UpdateTwoProductsInteractor 統合テスト
    // ----------------------------
    @Test
    void testUpdateTwoProductsSuccess() {
        UpdateTwoProductsInputData input = new UpdateTwoProductsInputData(
                PRODUCT_ID_IPHONE, PRICE_IPHONE_BD,
                PRODUCT_ID_GALAXY, PRICE_GALAXY_BD);

        UpdateTwoProductsOutputData output = updateTwoProductsInteractor.handle(input);

        assertFalse(output.isSuccess());
        assertEquals(UseCaseErrorCodes.ROLLBACK_ERROR, output.getErrorMessage());
    }
}