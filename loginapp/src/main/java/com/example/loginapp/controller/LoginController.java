package com.example.loginapp.controller;

import com.example.loginapp.dto.SuccessResponse;
import com.example.loginapp.aop.SessionRequired;
import com.example.loginapp.dto.ErrorResponse;
import com.example.loginapp.dto.LoginRequest;
import com.example.loginapp.dto.SessionCheckResponse;
import com.example.loginapp.entity.User;
import com.example.loginapp.service.UserService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.context.MessageSource;

import static com.example.loginapp.constants.MessageKeys.*;
import static com.example.loginapp.constants.SessionKeys.*;

import java.util.Locale;

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

        try {
            User user = userService.findUser(request.getUsername());

            if (user == null || !user.getPassword().equals(request.getPassword())) {
                String msg = messageSource.getMessage(ERROR_INVALID_CREDENTIALS, null, locale);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(msg));
            }

            session.setAttribute(IS_LOGGED_IN, true);
            session.setAttribute(USERNAME, user.getUsername());
            session.setAttribute(LOGIN_TIME, System.currentTimeMillis());

            String msg = messageSource.getMessage(SUCCESS_LOGIN, null, locale);
            return ResponseEntity.ok(new SuccessResponse(msg));

        } catch (Exception e) {
            e.printStackTrace();
            String msg = messageSource.getMessage(ERROR_INTERNAL_SERVER, null, locale);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(msg));
        }
    }

    /**
     * ログアウト処理を行う。
     *
     * @param session HttpSession
     * @param locale  ロケール情報
     * @return ログアウト完了メッセージ
     */
    @PostMapping("/logout")
    public ResponseEntity<SuccessResponse> logout(HttpSession session, Locale locale) {
        session.invalidate();
        String msg = messageSource.getMessage(SUCCESS_LOGOUT, null, locale);
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
        String msg = messageSource.getMessage(SUCCESS_SESSION_CHECK, null, locale);
        return ResponseEntity.ok(new SuccessResponse(msg));
    }
}