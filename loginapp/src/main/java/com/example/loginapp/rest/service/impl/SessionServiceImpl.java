package com.example.loginapp.rest.service.impl;

import com.example.loginapp.rest.config.SessionProperties;
import com.example.loginapp.rest.service.SessionService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

import static com.example.loginapp.rest.constants.SessionKeys.*;

import org.springframework.stereotype.Service;

/**
 * REST API で利用するセッション操作の実装クラス。
 * ログイン情報の登録、セッション無効化、有効期限チェックを行う。
 */
@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {

    /** セッションに関する設定値を保持するプロパティクラス */
    private final SessionProperties sessionProperties;

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

        if (!((Boolean) isLoggedIn)) {
            return false;
        }

        long elapsed = System.currentTimeMillis() - (Long) loginTime;
        return elapsed <= sessionProperties.getTimeoutMillis();
    }
}
