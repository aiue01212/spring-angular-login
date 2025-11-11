package com.example.loginapp.domain.service;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import static com.example.loginapp.constants.SessionKeys.*;

/**
 * セッション管理を行うサービスクラス。
 * ログイン状態の管理やセッション情報の保持・削除を担当する。
 */
@Service
public class SessionService {

    /** セッションの有効期限（ミリ秒） */
    private static final long SESSION_TIMEOUT_MILLIS = 60_000L;

    /**
     * ログイン情報をセッションに登録する。
     */
    public void createLoginSession(HttpSession session, String username) {
        session.setAttribute(IS_LOGGED_IN, true);
        session.setAttribute(USERNAME, username);
        session.setAttribute(LOGIN_TIME, System.currentTimeMillis());
    }

    /**
     * セッションを無効化（ログアウト処理）する。
     */
    public void invalidateSession(HttpSession session) {
        session.invalidate();
    }

    /**
     * セッションが有効かどうか判定する。
     *
     * @return true: 有効 / false: 無効または期限切れ
     */
    public boolean isSessionValid(HttpSession session) {
        Object isLoggedIn = session.getAttribute(IS_LOGGED_IN);
        Object loginTime = session.getAttribute(LOGIN_TIME);

        if (!(isLoggedIn instanceof Boolean) || !(loginTime instanceof Long)) {
            return false;
        }

        long elapsed = System.currentTimeMillis() - (Long) loginTime;
        return (Boolean) isLoggedIn && elapsed <= SESSION_TIMEOUT_MILLIS;
    }
}
