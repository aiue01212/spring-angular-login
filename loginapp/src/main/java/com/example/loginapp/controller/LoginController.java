package com.example.loginapp.controller;

import com.example.loginapp.dto.LoginRequest;
import com.example.loginapp.mapper.ProductMapper;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin(origins = { "http://localhost:9090", "http://localhost:4200" }, allowCredentials = "true")
@RestController
@RequestMapping("/api")
public class LoginController {

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpSession session) {

        // 特定の条件で意図的に500エラーを発生させる
        if ("error".equals(request.getUsername())) {
            throw new RuntimeException("サーバー内部エラーが発生しました");
        }

        // 認証ロジック
        if ("user".equals(request.getUsername()) && "pass".equals(request.getPassword())) {
            session.setAttribute("isLoggedIn", true);
            session.setAttribute("username", request.getUsername());
            session.setAttribute("loginTime", System.currentTimeMillis());
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().body(Map.of("error", "ユーザIDまたはパスワードが違います"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate(); // セッション破棄
        return ResponseEntity.ok(Map.of("message", "ログアウトしました"));
    }

    @GetMapping("/session-check")
    public ResponseEntity<?> sessionCheck(HttpSession session) {
        Boolean isLoggedIn = (Boolean) session.getAttribute("isLoggedIn");
        Long loginTime = (Long) session.getAttribute("loginTime");

        if (Boolean.TRUE.equals(isLoggedIn)) {
            // loginTime が null の場合も許容（テストで未設定の場合がある）
            if (loginTime != null) {
                long elapsed = System.currentTimeMillis() - loginTime;
                if (elapsed > 60 * 1000) { // 1分経過でセッション切れ
                    session.invalidate();
                    return ResponseEntity.status(401).body(Map.of("error", "セッション切れです"));
                }
            }
            return ResponseEntity.ok(Map.of("message", "ログイン中"));
        } else {
            return ResponseEntity.status(401).body(Map.of("error", "未ログインです"));
        }
    }

    @Autowired
    private ProductMapper productMapper;

    @GetMapping("/products")
    public ResponseEntity<?> getProducts(HttpSession session) {
        Boolean isLoggedIn = (Boolean) session.getAttribute("isLoggedIn");
        if (Boolean.TRUE.equals(isLoggedIn)) {
            return ResponseEntity.ok(productMapper.findAll());
        } else {
            return ResponseEntity.status(401).body(Map.of("error", "未ログインです"));
        }
    }
}