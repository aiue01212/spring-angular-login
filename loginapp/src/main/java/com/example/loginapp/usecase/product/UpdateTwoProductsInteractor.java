package com.example.loginapp.usecase.product;

import com.example.loginapp.domain.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 商品の2件更新（ロールバック確認用）を行う Interactor。
 */
@Service
@RequiredArgsConstructor
public class UpdateTwoProductsInteractor implements UpdateTwoProductsInputBoundary {

    /**
     * 商品取得や更新など、商品に関するビジネスロジックを提供するドメインサービス。
     */
    private final ProductService productService;

    @Override
    public UpdateTwoProductsOutputData handle(UpdateTwoProductsInputData input) {

        try {
            productService.updateTwoProductsWithRollback(
                    input.getId1(),
                    input.getPrice1(),
                    input.getId2(),
                    input.getPrice2());

        } catch (Exception e) {
            return new UpdateTwoProductsOutputData(false, e.getMessage());
        }

        return new UpdateTwoProductsOutputData(true, null);
    }
}
