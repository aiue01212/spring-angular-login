package com.example.loginapp.controller;

import com.example.loginapp.dto.LoginRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * ログイン・ログアウトおよびセッション確認を行うコントローラー。
 */
@CrossOrigin(origins = { "http://localhost:9090", "http://localhost:4200" }, allowCredentials = "true")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LoginController {

    /** セッション有効期限（ミリ秒） */
    private static final long SESSION_TIMEOUT_MILLIS = 60_000L;

    /**
     * ログイン処理を行う。
     *
     * @param request ログインリクエスト（ユーザー名・パスワード）
     * @param session HTTPセッション
     * @return ログイン成功またはエラーメッセージを含むレスポンス
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest request, HttpSession session) {

        // テスト用の意図的なエラー
        if ("error".equals(request.getUsername())) {
            throw new RuntimeException("サーバー内部エラーが発生しました");
        }

        // 認証ロジック
        if ("user".equals(request.getUsername()) && "pass".equals(request.getPassword())) {
            session.setAttribute("isLoggedIn", true);
            session.setAttribute("username", request.getUsername());
            session.setAttribute("loginTime", System.currentTimeMillis());
            return ResponseEntity.ok(Map.of("message", "ログインに成功しました"));
        } else {
            return ResponseEntity.badRequest().body(Map.of("error", "ユーザIDまたはパスワードが違います"));
        }
    }

    /**
     * ログアウト処理を行う。
     *
     * @param session HTTPセッション
     * @return ログアウト完了メッセージ
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(Map.of("message", "ログアウトしました"));
    }

    /**
     * 現在のセッション状態を確認する。
     *
     * @param session HTTPセッション
     * @return セッション状態を示すレスポンス
     */
    @GetMapping("/session-check")
    public ResponseEntity<Map<String, String>> sessionCheck(HttpSession session) {
        Boolean isLoggedIn = (Boolean) session.getAttribute("isLoggedIn");
        Long loginTime = (Long) session.getAttribute("loginTime");

        if (Boolean.TRUE.equals(isLoggedIn)) {
            if (loginTime != null) {
                long elapsed = System.currentTimeMillis() - loginTime;
                if (elapsed > SESSION_TIMEOUT_MILLIS) {
                    session.invalidate();
                    return ResponseEntity.status(401).body(Map.of("error", "セッション切れです"));
                }
            }
            return ResponseEntity.ok(Map.of("message", "ログイン中"));
        } else {
            return ResponseEntity.status(401).body(Map.of("error", "未ログインです"));
        }
    }
}