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

/**
 * ユースケース統合テストクラス。
 */
@SpringBootTest
public class UseCaseIntegrationTest {

    /**
     * ログインユースケースのインターフェース。
     */
    @Autowired
    private LoginInputBoundary loginInteractor;

    /**
     * 全商品取得ユースケースのインターフェース。
     */
    @Autowired
    private GetAllProductsInputBoundary getAllProductsInteractor;

    /**
     * 商品ID指定取得ユースケースのインターフェース。
     */
    @Autowired
    private GetProductByIdInputBoundary getProductByIdInteractor;

    /**
     * 2商品同時更新ユースケースのインターフェース。
     */
    @Autowired
    private UpdateTwoProductsInputBoundary updateTwoProductsInteractor;

    // ----------------------------
    // LoginInteractor 統合テスト
    // ----------------------------

    /**
     * 正しいユーザー名とパスワードでログインできることを検証する。
     */
    @Test
    void testLoginSuccess() {
        LoginInputData input = new LoginInputData(VALID_USERNAME, VALID_PASSWORD);
        LoginOutputData output = loginInteractor.login(input);

        assertTrue(output.isSuccess());
        assertEquals(VALID_USERNAME, output.getUsername());
        assertNull(output.getErrorCode());
    }

    /**
     * パスワードが間違っている場合、ログイン失敗となることを検証する。
     */
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

    /**
     * 全商品の取得が正常に行えることを検証する。
     * 取得した商品に iPhone と Galaxy が含まれていることを確認する。
     */
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

    /**
     * ID指定で iPhone を取得できることを検証する。
     */
    @Test
    void testGetProductByIdSuccess() {
        GetProductByIdOutputData output = getProductByIdInteractor
                .handle(new GetProductByIdInputData(PRODUCT_ID_IPHONE));

        assertTrue(output.isSuccess());
        assertNotNull(output.getProduct());
        assertEquals(PRODUCT_ID_IPHONE, output.getProduct().getId());
    }

    /**
     * 存在しない商品IDを指定した場合、適切にエラーが返ることを検証する。
     */
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

    /**
     * 2商品同時更新が正常に動作するかを検証する。
     * ※このテストでは実際のトランザクションロールバックの挙動を確認。
     */
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