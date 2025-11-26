package com.example.loginapp.rest.service.impl;

import com.example.loginapp.rest.service.SessionService;

import jakarta.servlet.http.HttpSession;

import static com.example.loginapp.rest.constants.SessionKeys.*;

import org.springframework.stereotype.Service;

/**
 * REST API で利用するセッション操作の実装クラス。
 * ログイン情報の登録、セッション無効化、有効期限チェックを行う。
 */
@Service
public class SessionServiceImpl implements SessionService {

    /** セッションの有効期限（ミリ秒） */
    private static final long SESSION_TIMEOUT_MILLIS = 60_000L;

    /**
     * ログイン情報をセッションに登録する。
     */
    @Override
    public void createLoginSession(HttpSession session, String username) {
        session.setAttribute(IS_LOGGED_IN, true);
        session.setAttribute(USERNAME, username);
        session.setAttribute(LOGIN_TIME, System.currentTimeMillis());
    }

    /**
     * セッションを無効化（ログアウト処理）する。
     */
    @Override
    public void invalidateSession(HttpSession session) {
        session.invalidate();
    }

    /**
     * セッションが有効かどうか判定する。
     *
     * @return true: 有効 / false: 無効または期限切れ
     */
    @Override
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
