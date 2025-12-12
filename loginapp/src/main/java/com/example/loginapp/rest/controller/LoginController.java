package com.example.loginapp.rest.controller;

import com.example.loginapp.domain.usecase.login.LoginInputBoundary;
import com.example.loginapp.domain.usecase.login.LoginInputData;
import com.example.loginapp.domain.usecase.login.LoginOutputData;
import com.example.loginapp.rest.annotation.SessionRequired;
import com.example.loginapp.rest.model.ErrorResponse;
import com.example.loginapp.rest.model.LoginRequest;
import com.example.loginapp.rest.model.SessionCheckResponse;
import com.example.loginapp.rest.model.SuccessResponse;
import com.example.loginapp.rest.service.SessionService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.context.MessageSource;

import static com.example.loginapp.domain.constants.MessageKeys.*;
import static com.example.loginapp.domain.usecase.constants.UseCaseErrorCodes.*;

import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ログイン・ログアウトおよびセッション確認を行うコントローラー。
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LoginController {

    /** メッセージソース */
    private final MessageSource messageSource;

    /** ログイン処理の UseCase（Interactor） */
    private final LoginInputBoundary loginUseCase;

    /** セッションサービス */
    private final SessionService sessionService;

    /** ログ出力用のLogger */
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

        LoginInputData inputData = new LoginInputData(request.getUsername(), request.getPassword());
        LoginOutputData outputData = loginUseCase.login(inputData);

        if (outputData.isSuccess()) {
            sessionService.createLoginSession(session, outputData.getUsername());
            String msg = messageSource.getMessage(SUCCESS_LOGIN, null, locale);
            log.info("API終了: /login");
            return ResponseEntity.ok(new SuccessResponse(msg));
        }

        if (INVALID_CREDENTIALS.equals(outputData.getErrorCode())) {
            String errorMsg = messageSource.getMessage(ERROR_INVALID_CREDENTIALS, null, locale);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(errorMsg));
        }

        String errorMsg = messageSource.getMessage(ERROR_INTERNAL_SERVER, null, locale);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(errorMsg));

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

        sessionService.invalidateSession(session);

        String msg = messageSource.getMessage(SUCCESS_LOGOUT, null, locale);
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