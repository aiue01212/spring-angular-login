package com.example.loginapp.constants;

/**
 * メッセージキーの定数クラス。
 * メッセージプロパティのキーをここで一元管理する。
 */
public final class MessageKeys {

    private MessageKeys() {
    }

    // エラーメッセージ
    public static final String ERROR_NOT_LOGGED_IN = "error.notLoggedIn";
    public static final String ERROR_SESSION_EXPIRED = "error.sessionExpired";
    public static final String ERROR_INVALID_CREDENTIALS = "error.invalidCredentials";
    public static final String ERROR_INTERNAL_SERVER = "error.internalServerError";
    public static final String ERROR_PRODUCT_NOT_FOUND = "error.productNotFound";
    public static final String ERROR_MISSING_HTTP_SESSION = "error.missingHttpSession";

    // 成功メッセージ
    public static final String SUCCESS_LOGIN = "success.login";
    public static final String SUCCESS_LOGOUT = "success.logout";
    public static final String SUCCESS_PROCESS = "success.process";
    public static final String SUCCESS_SESSION_CHECK = "success.sessionCheck";
}
