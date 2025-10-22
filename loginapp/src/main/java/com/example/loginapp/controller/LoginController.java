package com.example.loginapp.controller;

import com.example.loginapp.dto.LoginRequest;

import jakarta.servlet.http.HttpSession;

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
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", "ユーザIDまたはパスワードが違います"));
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
        if (Boolean.TRUE.equals(isLoggedIn)) {
            return ResponseEntity.ok(Map.of("message", "ログイン中"));
        } else {
            return ResponseEntity.status(401).body(Map.of("error", "未ログインです"));
        }
    }
}