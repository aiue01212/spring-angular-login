package com.example.loginapp.controller;

import com.example.loginapp.entity.Product;
import com.example.loginapp.mapper.ProductMapper;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 商品情報を管理するコントローラークラス。
 * <p>
 * ログイン済みユーザーのみがアクセス可能です。
 */
@CrossOrigin(origins = { "http://localhost:9090", "http://localhost:4200" }, allowCredentials = "true")
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    /** 商品データアクセスを行うマッパー */
    private final ProductMapper productMapper;

    /**
     * 商品一覧を取得する。
     * <p>
     * ログイン済みでない場合はエラーを返す。
     * </p>
     *
     * @param session HTTPセッション
     * @return 商品一覧またはエラーメッセージ
     */
    @GetMapping
    public ResponseEntity<Object> getProducts(HttpSession session) {
        Boolean isLoggedIn = (Boolean) session.getAttribute("isLoggedIn");

        if (Boolean.TRUE.equals(isLoggedIn)) {
            List<Product> products = productMapper.findAll();
            return ResponseEntity.ok(products);
        } else {
            return ResponseEntity.status(401)
                    .body(Map.of("error", "未ログインです"));
        }
    }

    /**
     * 商品IDを指定して商品情報を取得する。
     * <p>
     * ログイン済みでない場合はエラーを返す。
     * </p>
     *
     * @param id      商品ID
     * @param session HTTPセッション
     * @return 商品情報またはエラーメッセージ
     */
    @GetMapping("/{id}")
    public ResponseEntity<Object> getProductById(@PathVariable int id, HttpSession session) {
        Boolean isLoggedIn = (Boolean) session.getAttribute("isLoggedIn");

        if (Boolean.TRUE.equals(isLoggedIn)) {
            Product product = productMapper.findById(id);
            if (product != null) {
                return ResponseEntity.ok(product);
            } else {
                return ResponseEntity.status(404)
                        .body(Map.of("error", "指定された商品が見つかりません"));
            }
        } else {
            return ResponseEntity.status(401)
                    .body(Map.of("error", "未ログインです"));
        }
    }
}
