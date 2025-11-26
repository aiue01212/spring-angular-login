package com.example.loginapp.rest.interceptor;

import com.example.loginapp.rest.annotation.SessionRequired;
import com.example.loginapp.rest.config.SessionProperties;
import com.example.loginapp.rest.model.ErrorResponse;
import com.example.loginapp.rest.model.SessionCheckResponse;
import com.example.loginapp.rest.model.SuccessResponse;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import static com.example.loginapp.rest.constants.MessageKeys.*;
import static com.example.loginapp.rest.constants.SessionKeys.*;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
     * ログ出力用のLogger
     */
    private static final Logger log = LoggerFactory.getLogger(SessionValidator.class);

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

        String methodName = joinPoint.getSignature().toShortString();
        log.info("API開始: {}", methodName);

        HttpSession session = extractSession(joinPoint);
        Locale locale = extractLocale(joinPoint);

        if (session == null) {
            log.error("HttpSessionが見つかりません: {}", methodName);
            throw new IllegalStateException(
                    messageSource.getMessage(ERROR_MISSING_HTTP_SESSION, null, locale));
        }

        if (isNotLoggedIn(session)) {
            log.warn("未ログイン状態でアクセス: {}", methodName);
            return unauthorized(ERROR_NOT_LOGGED_IN, locale);
        }

        if (isSessionExpired(session)) {
            try {
                session.invalidate();
            } catch (IllegalStateException e) {
                log.error("セッション無効化中に例外発生: {}", methodName, e);
            }
            log.warn("セッション有効期限切れ: {}", methodName);
            return unauthorized(ERROR_SESSION_EXPIRED, locale);
        }

        Object result = proceedJoinPoint(joinPoint, methodName, locale);

        if (result instanceof ResponseEntity responseEntity) {
            if (responseEntity.getBody() instanceof SessionCheckResponse) {
                @SuppressWarnings("unchecked")
                ResponseEntity<SessionCheckResponse> casted = (ResponseEntity<SessionCheckResponse>) responseEntity;
                log.info("API終了: {}", methodName);
                return casted;
            }
        }

        String msg = messageSource.getMessage(SUCCESS_PROCESS, null, locale);
        log.info("API終了: {}", methodName);
        return ResponseEntity.ok(new SuccessResponse(msg));
    }

    /**
     * joinPointの実行を行い、例外処理を共通化する。
     *
     * @param joinPoint  対象メソッド
     * @param methodName ログ出力用のメソッド名
     * @param locale     メッセージ取得用ロケール
     * @return 実行結果オブジェクト
     * @throws Exception 実行時に発生した例外をラップして再スロー
     */
    private Object proceedJoinPoint(ProceedingJoinPoint joinPoint, String methodName, Locale locale) throws Exception {
        try {
            return joinPoint.proceed();
        } catch (DataAccessException e) {
            log.error("DBアクセス中に例外発生: {}", methodName, e);
            String msg = messageSource.getMessage(ERROR_DATABASE_ACCESS, null, locale);
            throw new Exception(msg, e);
        } catch (IllegalArgumentException e) {
            log.error("引数不正例外発生: {}", methodName, e);
            String msg = messageSource.getMessage(ERROR_PROCEED_JOINPOINT_FAILED, null, locale)
                    + ": " + e.getMessage();
            throw new Exception(msg, e);
        } catch (RuntimeException e) {
            log.error("処理中に予期せぬランタイム例外発生: {}", methodName, e);
            String baseMsg = messageSource.getMessage(ERROR_PROCEED_JOINPOINT_FAILED, null, locale);
            String message = baseMsg + ": " + (e.getMessage() != null ? e.getMessage() : e.toString());
            throw new Exception(message, e);
        } catch (Throwable t) {
            log.error("処理中に予期せぬ例外発生: {}", methodName, t);
            String baseMsg = messageSource.getMessage(ERROR_PROCEED_JOINPOINT_FAILED, null, locale);
            String message = baseMsg + ": " + (t.getMessage() != null ? t.getMessage() : t.toString());
            throw new Exception(message, t);
        }
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