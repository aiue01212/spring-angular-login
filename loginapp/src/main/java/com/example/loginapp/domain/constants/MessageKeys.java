package com.example.loginapp.domain.constants;

/**
 * メッセージキーの定数クラス。
 * メッセージプロパティのキーをここで一元管理する。
 */
public final class MessageKeys {

    private MessageKeys() {
    }

    /**
     * エラーメッセージ
     */

    /** 未ログインエラー */
    public static final String ERROR_NOT_LOGGED_IN = "error.notLoggedIn";

    /** セッション期限切れ */
    public static final String ERROR_SESSION_EXPIRED = "error.sessionExpired";

    /** 認証失敗 */
    public static final String ERROR_INVALID_CREDENTIALS = "error.invalidCredentials";

    /** サーバ内部エラー */
    public static final String ERROR_INTERNAL_SERVER = "error.internalServerError";

    /** 商品が見つからない */
    public static final String ERROR_PRODUCT_NOT_FOUND = "error.productNotFound";

    /** HTTPセッションが存在しない */
    public static final String ERROR_MISSING_HTTP_SESSION = "error.missingHttpSession";

    /** ロールバック発生 */
    public static final String ERROR_ROLLBACK_OCCURRED = "error.rollbackOccurred";

    /** ロールバックテスト用 */
    public static final String ERROR_ROLLBACK_TEST = "error.rollbackTest";

    /** セッションタイムアウトが負の値 */
    public static final String ERROR_NEGATIVE_SESSION_TIMEOUT = "error.negativeSessionTimeout";

    /** ProceedingJoinPoint.invoke 失敗 */
    public static final String ERROR_PROCEED_JOINPOINT_FAILED = "error.proceed.joinpoint.failed";

    /** データベースアクセスエラー */
    public static final String ERROR_DATABASE_ACCESS = "error.database.access";

    /** 商品 ID を指定したがデータが存在しない */
    public static final String ERROR_PRODUCT_NOT_FOUND_ID = "error.productNotFoundWithId";

    /**
     * 成功メッセージ
     */

    /** ログイン成功 */
    public static final String SUCCESS_LOGIN = "success.login";

    /** ログアウト成功 */
    public static final String SUCCESS_LOGOUT = "success.logout";

    /** 処理成功 */
    public static final String SUCCESS_PROCESS = "success.process";

    /** セッションチェック成功 */
    public static final String SUCCESS_SESSION_CHECK = "success.sessionCheck";

    /** ロールバック付き更新処理成功 */
    public static final String SUCCESS_UPDATE_WITH_ROLLBACK = "success.updateWithRollback";
}
