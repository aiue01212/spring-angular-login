package com.example.loginapp.rest.controller;

import com.example.loginapp.annotation.SessionRequired;
import com.example.loginapp.domain.entity.User;
import com.example.loginapp.domain.service.SessionService;
import com.example.loginapp.domain.service.UserService;
import com.example.loginapp.rest.model.ErrorResponse;
import com.example.loginapp.rest.model.LoginRequest;
import com.example.loginapp.rest.model.SessionCheckResponse;
import com.example.loginapp.rest.model.SuccessResponse;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataAccessException;

import static com.example.loginapp.constants.MessageKeys.*;

import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ログイン・ログアウトおよびセッション確認を行うコントローラー。
 */
@CrossOrigin(origins = "http://localhost:9090", allowCredentials = "true")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LoginController {

    /** メッセージソース */
    private final MessageSource messageSource;

    /** ユーザサービス */
    private final UserService userService;

    /**
     * セッションサービス。
     * 必要に応じてセッションの生成や破棄などを管理するサービス。
     */
    private final SessionService sessionService;

    /**
     * ログ出力用のLogger
     */
    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    /**
     * ログイン処理を行う。
     *
     * @param request ログイン情報
     * @param session HttpSession
     * @param locale  ロケール情報
     * @return 成功時はログイン成功メッセージ、失敗時はエラーメッセージ
     */
    @PostMapping("/login")
    public ResponseEntity<SessionCheckResponse> login(@RequestBody LoginRequest request, HttpSession session,
            Locale locale) {
        log.info("API開始: /login");

        // ユーザー取得部分だけ try-catch
        User user;
        try {
            user = userService.findUser(request.getUsername());
        } catch (DataAccessException e) {
            log.error("ユーザー取得中に例外発生", e);
            String msg = messageSource.getMessage(ERROR_INTERNAL_SERVER, null, locale);
            return ResponseEntity.internalServerError().body(new ErrorResponse(msg));
        }

        if (user == null || !user.getPassword().equals(request.getPassword())) {
            String msg = messageSource.getMessage(ERROR_INVALID_CREDENTIALS, null, locale);
            log.warn("認証失敗: {}", request.getUsername());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(msg));
        }

        try {
            sessionService.createLoginSession(session, user.getUsername());
        } catch (IllegalStateException e) {
            log.error("セッション作成中に例外発生", e);
            String msg = messageSource.getMessage(ERROR_INTERNAL_SERVER, null, locale);
            return ResponseEntity.internalServerError().body(new ErrorResponse(msg));
        }

        String msg = messageSource.getMessage(SUCCESS_LOGIN, null, locale);
        log.info("ログイン成功: {}", user.getUsername());
        log.info("API終了: /login");
        return ResponseEntity.ok(new SuccessResponse(msg));
    }

    /**
     * ログアウト処理を行う。
     *
     * @param session HttpSession
     * @param locale  ロケール情報
     * @return ログアウト完了メッセージ
     */
    @PostMapping("/logout")
    public ResponseEntity<SessionCheckResponse> logout(HttpSession session, Locale locale) {
        log.info("API開始: /logout");

        try {
            sessionService.invalidateSession(session);
        } catch (IllegalStateException e) {
            log.error("セッション無効化中に例外発生", e);
            String msg = messageSource.getMessage(ERROR_INTERNAL_SERVER, null, locale);
            return ResponseEntity.internalServerError().body(new ErrorResponse(msg));
        }

        // ここからは例外が想定されない処理
        String msg = messageSource.getMessage(SUCCESS_LOGOUT, null, locale);
        log.info("ログアウト完了");
        log.info("API終了: /logout");
        return ResponseEntity.ok(new SuccessResponse(msg));
    }

    /**
     * セッション状態を確認する。
     *
     * @param session HttpSession
     * @param locale  ロケール情報
     * @return ログイン中であれば成功メッセージ、未ログインまたはセッション切れの場合はエラーメッセージ
     */
    @GetMapping("/session-check")
    @SessionRequired
    public ResponseEntity<SuccessResponse> sessionCheck(HttpSession session, Locale locale) {
        log.info("API開始: /session-check");
        String msg = messageSource.getMessage(SUCCESS_SESSION_CHECK, null, locale);
        log.info("API終了: /session-check");
        return ResponseEntity.ok(new SuccessResponse(msg));
    }
}