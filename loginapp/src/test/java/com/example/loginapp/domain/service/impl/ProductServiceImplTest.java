package com.example.loginapp.domain.service.impl;

import com.example.loginapp.domain.model.Product;
import com.example.loginapp.domain.repository.ProductRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static com.example.loginapp.domain.constants.ExceptionMessages.ROLLBACK_TEST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    private static final int ID_IPHONE = 1;

    /** テスト用固定 ID2 */
    private static final int ID_GALAXY = 2;

    /** テスト用商品名 */
    private static final String NAME_IPHONE = "iPhone";
    private static final String NAME_GALAXY = "Galaxy";

    /** テスト用価格 */
    private static final BigDecimal PRICE_IPHONE = BigDecimal.valueOf(120000);
    private static final BigDecimal PRICE_GALAXY = BigDecimal.valueOf(98000);

    /** 価格更新テスト用値 */
    private static final BigDecimal PRICE_UPDATE_1 = BigDecimal.valueOf(1000.0);
    private static final BigDecimal PRICE_UPDATE_2 = BigDecimal.valueOf(2000.0);

    /**
     * テストや処理中に使用されるメッセージ定数をまとめた定義。
     */
    private static final int EXPECTED_PRODUCT_COUNT = 2;
    private static final int FIRST_INDEX = 0;
    private static final int ONCE = 1;

    /** 商品データの永続化にアクセスするリポジトリのモック。 */
    @Mock
    private ProductRepository productRepository;

    /** テスト対象の ProductServiceImpl（依存は Mockito により注入）。 */
    @InjectMocks
    private ProductServiceImpl productService;

    /**
     * findAll() が商品一覧を返すことを確認する。
     */
    @Test
    void getAllProducts_ReturnsProductList() {
        List<Product> mockList = Arrays.asList(
                new Product(ID_IPHONE, NAME_IPHONE, PRICE_IPHONE),
                new Product(ID_GALAXY, NAME_GALAXY, PRICE_GALAXY));

        when(productRepository.findAll()).thenReturn(mockList);

        List<Product> result = productService.getAllProducts();

        assertThat(result).hasSize(EXPECTED_PRODUCT_COUNT);
        assertThat(result.get(FIRST_INDEX).getName()).isEqualTo(NAME_IPHONE);
        verify(productRepository, times(ONCE)).findAll();
    }

    /**
     * findById() が指定 ID の商品を返すことを確認する。
     */
    @Test
    void getProductById_ReturnsProduct() {
        Product product = new Product(ID_IPHONE, NAME_IPHONE, PRICE_IPHONE);

        when(productRepository.findById(ID_IPHONE)).thenReturn(product);

        Product result = productService.getProductById(ID_IPHONE);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(NAME_IPHONE);
    }

    /**
     * 2 商品の更新後、強制的に RuntimeException が発生し、
     * トランザクションロールバックが意図通り動作することを確認する。
     */
    @Test
    void updateTwoProductsWithRollback_ThrowsException() {

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> productService.updateTwoProductsWithRollback(
                        ID_IPHONE, PRICE_UPDATE_1,
                        ID_GALAXY, PRICE_UPDATE_2));

        assertThat(ex.getMessage()).isEqualTo(ROLLBACK_TEST);

        verify(productRepository, times(ONCE)).updatePrice(ID_IPHONE, PRICE_UPDATE_1);
        verify(productRepository, times(ONCE)).updatePrice(ID_GALAXY, PRICE_UPDATE_2);
    }
}
