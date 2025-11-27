package com.example.loginapp.domain.service.impl;

import com.example.loginapp.domain.model.Product;
import com.example.loginapp.domain.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static com.example.loginapp.rest.constants.MessageKeys.ERROR_ROLLBACK_TEST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * {@link ProductServiceImpl} の動作を検証する単体テストクラス。
 * <p>
 * 取得処理、ID 指定取得、ロールバックを伴う例外発生動作を確認する。
 * </p>
 */
@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    /** テスト用固定 ID1 */
    private static final int PRODUCT_ID_1 = 1;

    /** テスト用固定 ID2 */
    private static final int PRODUCT_ID_2 = 2;

    /** テスト用商品名 */
    private static final String PRODUCT_NAME_IPHONE = "iPhone";
    private static final String PRODUCT_NAME_GALAXY = "Galaxy";

    /** テスト用価格 */
    private static final int PRICE_IPHONE = 120000;
    private static final int PRICE_GALAXY = 98000;

    /** ロールバックテスト用メッセージ */
    private static final String MSG_ROLLBACK_ERROR = "Rollback test error";

    /** 価格更新テスト用値 */
    private static final double PRICE_UPDATE_1 = 1000.0;
    private static final double PRICE_UPDATE_2 = 2000.0;

    /** 商品データの永続化にアクセスするリポジトリのモック。 */
    @Mock
    private ProductRepository productRepository;

    /** メッセージ解決に使用する MessageSource のモック。 */
    @Mock
    private MessageSource messageSource;

    /** テスト対象の ProductServiceImpl（依存は Mockito により注入）。 */
    @InjectMocks
    private ProductServiceImpl productService;

    /**
     * テストや処理中に使用されるメッセージ定数をまとめた定義。
     */
    private static final int EXPECTED_PRODUCT_COUNT = 2;
    private static final int FIRST_INDEX = 0;
    private static final int ONCE = 1;

    /**
     * findAll() が商品一覧を返すことを確認する。
     */
    @Test
    void getAllProducts_ReturnsProductList() {
        List<Product> mockList = Arrays.asList(
                new Product(PRODUCT_ID_1, PRODUCT_NAME_IPHONE, PRICE_IPHONE),
                new Product(PRODUCT_ID_2, PRODUCT_NAME_GALAXY, PRICE_GALAXY));

        when(productRepository.findAll()).thenReturn(mockList);

        List<Product> result = productService.getAllProducts();

        assertThat(result).hasSize(EXPECTED_PRODUCT_COUNT);
        assertThat(result.get(FIRST_INDEX).getName()).isEqualTo(PRODUCT_NAME_IPHONE);
        verify(productRepository, times(ONCE)).findAll();
    }

    /**
     * findById() が指定 ID の商品を返すことを確認する。
     */
    @Test
    void getProductById_ReturnsProduct() {
        Product product = new Product(PRODUCT_ID_1, PRODUCT_NAME_IPHONE, PRICE_IPHONE);

        when(productRepository.findById(PRODUCT_ID_1)).thenReturn(product);

        Product result = productService.getProductById(PRODUCT_ID_1);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(PRODUCT_NAME_IPHONE);
    }

    /**
     * 2 商品の更新後、強制的に RuntimeException が発生し、
     * トランザクションロールバックが意図通り動作することを確認する。
     */
    @Test
    void updateTwoProductsWithRollback_ThrowsRuntimeException() {
        when(messageSource.getMessage(eq(ERROR_ROLLBACK_TEST), any(), any(Locale.class)))
                .thenReturn(MSG_ROLLBACK_ERROR);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> productService.updateTwoProductsWithRollback(
                        PRODUCT_ID_1, PRICE_UPDATE_1,
                        PRODUCT_ID_2, PRICE_UPDATE_2));

        assertThat(ex.getMessage()).isEqualTo(MSG_ROLLBACK_ERROR);

        verify(productRepository, times(ONCE)).updatePrice(PRODUCT_ID_1, PRICE_UPDATE_1);
        verify(productRepository, times(ONCE)).updatePrice(PRODUCT_ID_2, PRICE_UPDATE_2);
    }
}
