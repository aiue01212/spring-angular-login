package com.example.loginapp.domain.usecase.product;

import com.example.loginapp.domain.model.Product;
import com.example.loginapp.domain.service.ProductService;
import lombok.RequiredArgsConstructor;

import static com.example.loginapp.domain.constants.MessageKeys.*;

/**
 * 商品ID指定取得処理を実行する Interactor。
 */
@RequiredArgsConstructor
public class GetProductByIdInteractor implements GetProductByIdInputBoundary {

    /**
     * 商品取得や更新など、商品に関するビジネスロジックを提供するドメインサービス。
     */
    private final ProductService productService;

    @Override
    public GetProductByIdOutputData handle(GetProductByIdInputData input) {

        Product product = productService.getProductById(input.getProductId());

        if (product == null) {
            return new GetProductByIdOutputData(false, null, ERROR_PRODUCT_NOT_FOUND_ID,
                    String.valueOf(input.getProductId()));
        }

        return new GetProductByIdOutputData(true, product, null, null);
    }
}
