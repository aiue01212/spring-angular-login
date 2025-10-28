package com.example.loginapp.controller;

import com.example.loginapp.aspect.SessionRequired;
import com.example.loginapp.dto.ErrorResponse;
import com.example.loginapp.entity.Product;
import com.example.loginapp.mapper.ProductMapper;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品情報を管理するコントローラークラス。
 */
@CrossOrigin(origins = "http://localhost:9090", allowCredentials = "true")
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductMapper productMapper;

    /**
     * 商品一覧を取得する。
     *
     * @param session HttpSession
     * @return ログインしていれば商品一覧、未ログインならエラー
     */
    @GetMapping
    @SessionRequired
    public ResponseEntity<List<Product>> getProducts(HttpSession session) {
        List<Product> products = productMapper.findAll();
        return ResponseEntity.ok(products);
    }

    /**
     * 商品ID指定で商品情報を取得する。
     *
     * @param id      商品ID
     * @param session HttpSession
     * @return 商品情報またはエラーメッセージ
     */
    @GetMapping("/{id}")
    @SessionRequired
    public ResponseEntity<Object> getProductById(@PathVariable int id, HttpSession session) {
        Product product = productMapper.findById(id);
        if (product != null) {
            return ResponseEntity.ok(product);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("指定された商品が見つかりません"));
        }
    }
}