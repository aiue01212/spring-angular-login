package com.example.loginapp.domain.usecase.product;

import com.example.loginapp.domain.model.Product;
import com.example.loginapp.domain.service.ProductService;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * 全商品取得処理を実行する Interactor（UseCase の実装）。
 * ドメインサービスを利用して商品を取得し、
 * OutputData を生成して返す。
 */
@RequiredArgsConstructor
public class GetAllProductsInteractor implements GetAllProductsInputBoundary {

    /**
     * 商品取得や更新など、商品に関するビジネスロジックを提供するドメインサービス。
     */
    private final ProductService productService;

    @Override
    public GetAllProductsOutputData handle(GetAllProductsInputData input) {

        List<Product> products = productService.getAllProducts();

        return new GetAllProductsOutputData(true, products, null, null);
    }
}
