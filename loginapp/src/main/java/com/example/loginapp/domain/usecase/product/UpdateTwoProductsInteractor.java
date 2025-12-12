package com.example.loginapp.domain.usecase.product;

import com.example.loginapp.domain.service.ProductService;
import lombok.RequiredArgsConstructor;

/**
 * 商品の2件更新（ロールバック確認用）を行う Interactor。
 */
@RequiredArgsConstructor
public class UpdateTwoProductsInteractor implements UpdateTwoProductsInputBoundary {

    /**
     * 商品取得や更新など、商品に関するビジネスロジックを提供するドメインサービス。
     */
    private final ProductService productService;

    @Override
    public UpdateTwoProductsOutputData handle(UpdateTwoProductsInputData input) {

        productService.updateTwoProductsWithRollback(
                input.getProductId1(), input.getNewPrice1(),
                input.getProductId2(), input.getNewPrice2());

        return new UpdateTwoProductsOutputData(true, null);

    }
}
