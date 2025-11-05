package com.example.loginapp.aop;

import com.example.loginapp.annotation.SessionRequired;
import com.example.loginapp.config.SessionProperties;
import com.example.loginapp.dto.ErrorResponse;
import com.example.loginapp.dto.SessionCheckResponse;
import com.example.loginapp.dto.SuccessResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import static com.example.loginapp.constants.MessageKeys.*;
import static com.example.loginapp.constants.SessionKeys.*;

import java.util.Locale;

/**
 * {@code @SessionRequired} が付与されたメソッド実行時に
 * セッションのログイン状態および有効期限を検証する AOP クラス。
 */
@Aspect
@Component
@RequiredArgsConstructor
public class SessionValidator {

    /** メッセージソース */
    private final MessageSource messageSource;

    /** セッション設定情報 */
    private final SessionProperties sessionProperties;

    /**
     * セッションの有効性を確認し、必要に応じてエラーレスポンスを返す。
     *
     * @param joinPoint       対象メソッド情報
     * @param sessionRequired セッション必須アノテーション
     * @return 処理結果またはエラーレスポンス
     * @throws Exception 対象メソッド呼び出し時に例外が発生した場合
     */
    @Around("@annotation(sessionRequired)")
    public ResponseEntity<SessionCheckResponse> checkSession(
            ProceedingJoinPoint joinPoint,
            SessionRequired sessionRequired) throws Exception {

        HttpSession session = extractSession(joinPoint);
        Locale locale = extractLocale(joinPoint);

        if (session == null) {
            throw new IllegalStateException(
                    messageSource.getMessage(ERROR_MISSING_HTTP_SESSION, null, locale));
        }

        if (isNotLoggedIn(session)) {
            return unauthorized(ERROR_NOT_LOGGED_IN, locale);
        }

        if (isSessionExpired(session)) {
            session.invalidate();
            return unauthorized(ERROR_SESSION_EXPIRED, locale);
        }

        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Throwable t) {
            String baseMsg = messageSource.getMessage(ERROR_PROCEED_JOINPOINT_FAILED, null, locale);
            String errorMsg = baseMsg + ": " + (t.getMessage() != null ? t.getMessage() : t.toString());
            throw new Exception(errorMsg, t);
        }

        if (result instanceof ResponseEntity responseEntity) {
            if (responseEntity.getBody() instanceof SessionCheckResponse) {
                @SuppressWarnings("unchecked")
                ResponseEntity<SessionCheckResponse> casted = (ResponseEntity<SessionCheckResponse>) responseEntity;
                return casted;
            }
        }

        String msg = messageSource.getMessage(SUCCESS_PROCESS, null, locale);
        return ResponseEntity.ok(new SuccessResponse(msg));
    }

    /**
     * joinPoint から HttpSession を抽出する。
     */
    private HttpSession extractSession(ProceedingJoinPoint joinPoint) {
        for (Object arg : joinPoint.getArgs()) {
            if (arg instanceof HttpSession session) {
                return session;
            }
        }
        return null;
    }

    /**
     * joinPoint から Locale を抽出する。
     */
    private Locale extractLocale(ProceedingJoinPoint joinPoint) {
        for (Object arg : joinPoint.getArgs()) {
            if (arg instanceof Locale locale) {
                return locale;
            }
        }
        return Locale.getDefault();
    }

    /**
     * セッションがログイン済みでない場合に true を返す。
     */
    private boolean isNotLoggedIn(HttpSession session) {
        Boolean isLoggedIn = (Boolean) session.getAttribute(IS_LOGGED_IN);
        Object loginTime = session.getAttribute(LOGIN_TIME);
        return !Boolean.TRUE.equals(isLoggedIn) || !(loginTime instanceof Number);
    }

    /**
     * セッションの有効期限切れを判定する。
     */
    private boolean isSessionExpired(HttpSession session) {
        Long loginTime = (Long) session.getAttribute(LOGIN_TIME);
        long elapsed = System.currentTimeMillis() - loginTime;
        return elapsed > sessionProperties.getTimeoutMillis();
    }

    /**
     * 401 Unauthorized を返す共通メソッド。
     */
    private ResponseEntity<SessionCheckResponse> unauthorized(String messageKey, Locale locale) {
        String msg = messageSource.getMessage(messageKey, null, locale);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(msg));
    }
}