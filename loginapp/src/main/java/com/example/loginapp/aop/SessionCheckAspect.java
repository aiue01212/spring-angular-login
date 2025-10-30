package com.example.loginapp.aop;

import com.example.loginapp.config.SessionProperties;
import com.example.loginapp.dto.ErrorResponse;
import com.example.loginapp.dto.SessionCheckResponse;
import com.example.loginapp.dto.SuccessResponse;
import jakarta.servlet.http.HttpSession;
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
public class SessionCheckAspect {

    /** メッセージソース */
    private final MessageSource messageSource;

    /** セッション設定情報 */
    private final SessionProperties sessionProperties;

    /**
     * コンストラクタ。
     *
     * @param messageSource     メッセージリソース
     * @param sessionProperties セッション設定情報
     */
    public SessionCheckAspect(MessageSource messageSource, SessionProperties sessionProperties) {
        this.messageSource = messageSource;
        this.sessionProperties = sessionProperties;
    }

    /**
     * セッションの有効性を確認し、必要に応じてエラーレスポンスを返す。
     *
     * @param joinPoint       対象メソッド情報
     * @param sessionRequired セッション必須アノテーション
     * @return 対象メソッドの結果、またはエラーレスポンス
     * @throws Throwable 処理中に発生した例外
     */
    @Around("@annotation(sessionRequired)")
    public ResponseEntity<SessionCheckResponse> checkSession(
            ProceedingJoinPoint joinPoint,
            SessionRequired sessionRequired) throws Throwable {

        HttpSession session = null;
        Locale locale = Locale.getDefault();

        for (Object arg : joinPoint.getArgs()) {
            if (arg instanceof HttpSession) {
                session = (HttpSession) arg;
            } else if (arg instanceof Locale) {
                locale = (Locale) arg;
            }
        }

        if (session == null) {
            String msg = messageSource.getMessage(ERROR_MISSING_HTTP_SESSION, null, locale);
            throw new IllegalStateException(msg);
        }

        Boolean isLoggedIn = (Boolean) session.getAttribute(IS_LOGGED_IN);
        Object loginTimeObj = session.getAttribute(LOGIN_TIME);
        Long loginTime = null;
        if (loginTimeObj instanceof Number number) {
            loginTime = number.longValue();
        }

        if (!Boolean.TRUE.equals(isLoggedIn) || loginTime == null) {
            String msg = messageSource.getMessage(ERROR_NOT_LOGGED_IN, null, locale);
            ErrorResponse response = new ErrorResponse(msg);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        long elapsedMillis = System.currentTimeMillis() - loginTime;
        long timeoutMillis = sessionProperties.getTimeoutMillis();
        if (elapsedMillis > timeoutMillis) {
            session.invalidate();
            String msg = messageSource.getMessage(ERROR_SESSION_EXPIRED, null, locale);
            ErrorResponse response = new ErrorResponse(msg);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        Object result = joinPoint.proceed();

        if (result instanceof ResponseEntity) {
            @SuppressWarnings("unchecked")
            ResponseEntity<SessionCheckResponse> entity = (ResponseEntity<SessionCheckResponse>) result;
            return entity;
        }

        String msg = messageSource.getMessage(SUCCESS_PROCESS, null, locale);
        SuccessResponse success = new SuccessResponse(msg);
        return ResponseEntity.ok(success);
    }
}