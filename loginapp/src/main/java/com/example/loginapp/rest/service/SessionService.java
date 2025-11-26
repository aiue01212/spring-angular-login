package com.example.loginapp.rest.service;

import jakarta.servlet.http.HttpSession;

/**
 * REST 層で使用されるセッション操作を提供するサービスインターフェース。
 * ログイン情報の登録、セッションの無効化、セッションの有効性判定を定義する。
 */
public interface SessionService {

    /**
     * ログイン情報をセッションに保存する。
     *
     * @param session  HttpSession
     * @param username ログインしたユーザ名
     */
    void createLoginSession(HttpSession session, String username);

    /**
     * セッションを無効化（ログアウト）する。
     *
     * @param session HttpSession
     */
    void invalidateSession(HttpSession session);

    /**
     * セッションが有効かどうか判定する。
     *
     * @param session HttpSession
     * @return true: 有効 / false: 無効 or 期限切れ
     */
    boolean isSessionValid(HttpSession session);
}