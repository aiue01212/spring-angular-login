package com.example.loginapp.controller;

import com.example.loginapp.aspect.SessionRequired;
import com.example.loginapp.dto.ApiResponse;
import com.example.loginapp.dto.ErrorResponse;
import com.example.loginapp.dto.LoginRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * ログイン・ログアウトおよびセッション確認を行うコントローラー。
 */
@CrossOrigin(origins = "http://localhost:9090", allowCredentials = "true")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LoginController {

    /** セッションキー: ログイン状態 */
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    /** セッションキー: ユーザー名 */
    private static final String KEY_USERNAME = "username";
    /** セッションキー: ログイン時間 */
    private static final String KEY_LOGIN_TIME = "loginTime";

    /**
     * ログイン処理を行う。
     *
     * @param request ログイン情報
     * @param session HttpSession
     * @return 成功時はログイン成功メッセージ、失敗時はエラーメッセージ
     */
    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody LoginRequest request, HttpSession session) {

        // テスト用の意図的なエラー
        if ("error".equals(request.getUsername())) {
            throw new RuntimeException("サーバー内部エラーが発生しました");
        }

        // 認証ロジック
        if ("user".equals(request.getUsername()) && "pass".equals(request.getPassword())) {
            session.setAttribute(KEY_IS_LOGGED_IN, true);
            session.setAttribute(KEY_USERNAME, request.getUsername());
            session.setAttribute(KEY_LOGIN_TIME, System.currentTimeMillis());
            return ResponseEntity.ok(new ApiResponse("ログインに成功しました"));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("ユーザIDまたはパスワードが違います"));
        }
    }

    /**
     * ログアウト処理を行う。
     *
     * @param session HttpSession
     * @return ログアウト完了メッセージ
     */
    @PostMapping("/logout")
    public ResponseEntity<Object> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(new ApiResponse("ログアウトしました"));
    }

    /**
     * セッション状態を確認する。
     *
     * @param session HttpSession
     * @return ログイン中であれば成功メッセージ、未ログインまたはセッション切れの場合はエラーメッセージ
     */
    @GetMapping("/session-check")
    @SessionRequired
    public ResponseEntity<Object> sessionCheck(HttpSession session) {
        return ResponseEntity.ok(new ApiResponse("ログイン中"));
    }
}