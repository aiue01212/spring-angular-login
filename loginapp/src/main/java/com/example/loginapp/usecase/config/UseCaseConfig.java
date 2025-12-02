package com.example.loginapp.usecase.config;

import com.example.loginapp.domain.service.ProductService;
import com.example.loginapp.domain.service.UserService;
import com.example.loginapp.usecase.login.LoginInputBoundary;
import com.example.loginapp.usecase.login.LoginInteractor;
import com.example.loginapp.usecase.product.GetAllProductsInputBoundary;
import com.example.loginapp.usecase.product.GetAllProductsInteractor;
import com.example.loginapp.usecase.product.GetProductByIdInputBoundary;
import com.example.loginapp.usecase.product.GetProductByIdInteractor;
import com.example.loginapp.usecase.product.UpdateTwoProductsInputBoundary;
import com.example.loginapp.usecase.product.UpdateTwoProductsInteractor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    public LoginInputBoundary loginInteractor(UserService userService) {
        return new LoginInteractor(userService);
    }

    @Bean
    public GetAllProductsInputBoundary getAllProductsInteractor(ProductService productService) {
        return new GetAllProductsInteractor(productService);
    }

    @Bean
    public GetProductByIdInputBoundary getProductByIdInteractor(ProductService productService) {
        return new GetProductByIdInteractor(productService);
    }

    @Bean
    public UpdateTwoProductsInputBoundary updateTwoProductsInteractor(ProductService productService) {
        return new UpdateTwoProductsInteractor(productService);
    }
}
