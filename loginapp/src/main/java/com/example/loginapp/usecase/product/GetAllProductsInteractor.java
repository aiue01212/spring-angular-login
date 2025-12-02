package com.example.loginapp.usecase.product;

import com.example.loginapp.domain.model.Product;
import com.example.loginapp.domain.service.ProductService;
import lombok.RequiredArgsConstructor;

import java.util.List;
import org.springframework.dao.DataAccessException;

import static com.example.loginapp.usecase.constants.UseCaseErrorCodes.DB_ERROR;

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

        List<Product> products;

        try {
            products = productService.getAllProducts();
        } catch (DataAccessException e) {
            return new GetAllProductsOutputData(false, List.of(), DB_ERROR, e.getMessage());
        }

        return new GetAllProductsOutputData(true, products, null, null);
    }
}
