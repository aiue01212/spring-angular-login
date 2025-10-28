package com.example.loginapp.aspect;

import jakarta.servlet.http.HttpSession;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * セッション状態とログイン有効期限をチェックする AOP。
 * <p>
 * 
 * @SessionRequired アノテーションが付与されたメソッド実行時に、
 *                  セッションが有効かどうかを確認します。
 *                  </p>
 */
@Aspect
@Component
public class SessionCheckAspect {

    /** セッション有効期限（ミリ秒） */
    private static final long SESSION_TIMEOUT_MILLIS = 60_000L;

    /** 未ログイン時のエラーメッセージ */
    private static final String ERROR_NOT_LOGGED_IN = "未ログインです";

    /** セッション切れ時のエラーメッセージ */
    private static final String ERROR_SESSION_EXPIRED = "セッション切れです";

    /**
     * @SessionRequired アノテーション付きメソッドの実行前に
     *                  ログイン状態と有効期限をチェックします。
     *
     * @param joinPoint       実行対象メソッド
     * @param sessionRequired @SessionRequired アノテーション
     * @param session         HTTPセッション
     * @return メソッド実行結果またはエラーレスポンス
     * @throws Throwable 実行対象メソッドで発生した例外
     */
    @Around("@annotation(sessionRequired) && args(..,session)")
    public ResponseEntity<Object> checkSession(
            ProceedingJoinPoint joinPoint,
            SessionRequired sessionRequired,
            HttpSession session) throws Throwable {

        Boolean isLoggedIn = (Boolean) session.getAttribute("isLoggedIn");
        Long loginTime = (Long) session.getAttribute("loginTime");

        // 未ログイン判定
        if (!Boolean.TRUE.equals(isLoggedIn) || loginTime == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", ERROR_NOT_LOGGED_IN));
        }

        // セッション有効期限チェック
        long elapsedMillis = System.currentTimeMillis() - loginTime;
        if (elapsedMillis > SESSION_TIMEOUT_MILLIS) {
            session.invalidate();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", ERROR_SESSION_EXPIRED));
        }

        // ログイン中かつ有効期限内の場合は通常処理を実行
        Object result = joinPoint.proceed();

        if (result instanceof ResponseEntity) {
            @SuppressWarnings("unchecked")
            ResponseEntity<Object> response = (ResponseEntity<Object>) result;
            return response;
        }

        return ResponseEntity.ok(Map.of("message", "処理が成功しました"));
    }
}