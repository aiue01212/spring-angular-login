package com.example.loginapp.rest.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.loginapp.domain.model.Product;
import com.example.loginapp.rest.constants.RepositoryConstants;

/**
 * MyBatisProductRepository の単体テスト
 */
@ExtendWith(MockitoExtension.class)
public class MyBatisProductRepositoryTest {

    @Mock
    private MyBatisProductRepository productRepository;

    /**
     * 全商品の取得テスト。
     */
    @Test
    void testFindAll() {
        when(productRepository.findAll()).thenReturn(RepositoryConstants.PRODUCT_LIST);
        List<Product> products = productRepository.findAll();
        assertNotNull(products);
        assertTrue(products.size() >= RepositoryConstants.EMPTY_LIST_SIZE);
    }

    /**
     * 商品IDによる検索テスト。
     */
    @Test
    void testFindById() {
        when(productRepository.findById(RepositoryConstants.PRODUCT_ID_1))
                .thenReturn(RepositoryConstants.PRODUCT_LIST.get(RepositoryConstants.FIRST_PRODUCT_INDEX));
        Product product = productRepository.findById(RepositoryConstants.PRODUCT_ID_1);
        assertNotNull(product);
        assertEquals(RepositoryConstants.PRODUCT_ID_1, product.getId());
    }

    /**
     * 商品価格更新テスト。
     */
    @Test
    void testUpdatePrice() {
        List<Product> productList = new ArrayList<>(RepositoryConstants.PRODUCT_LIST);

        when(productRepository.findById(RepositoryConstants.PRODUCT_ID_1))
                .thenAnswer(invocation -> productList.stream()
                        .filter(p -> p.getId() == RepositoryConstants.PRODUCT_ID_1)
                        .findFirst()
                        .orElse(null));

        doAnswer(invocation -> {
            int id = invocation.getArgument(RepositoryConstants.ARG_INDEX_ID);
            BigDecimal newPrice = invocation.getArgument(RepositoryConstants.ARG_INDEX_PRICE);
            productList.stream()
                    .filter(p -> p.getId() == id)
                    .findFirst()
                    .ifPresent(p -> p.setPrice(newPrice));
            return null;
        }).when(productRepository).updatePrice(anyInt(), any(BigDecimal.class));

        productRepository.updatePrice(RepositoryConstants.PRODUCT_ID_1, RepositoryConstants.PRODUCT_NEW_PRICE);
        Product updated = productRepository.findById(RepositoryConstants.PRODUCT_ID_1);

        assertNotNull(updated);
        assertEquals(RepositoryConstants.PRODUCT_NEW_PRICE, updated.getPrice());
    }

    /**
     * insertProductLog() のテスト
     */
    @Test
    void testInsertProductLog() {
        assertDoesNotThrow(() -> productRepository.insertProductLog(
                RepositoryConstants.PRODUCT_ID_1,
                RepositoryConstants.TEST_ACTION));
    }

}
