package com.example.loginapp.usecase.constants;

/**
 * UseCase 層で利用するエラーコードの定数クラス。
 */
public final class UseCaseErrorCodes {

    /** データベースアクセスエラー */
    public static final String DB_ERROR = "DB_ERROR";

    /** ログイン失敗（認証不一致） */
    public static final String INVALID_CREDENTIALS = "INVALID_CREDENTIALS";

    /** 商品が見つからない場合のエラー */
    public static final String PRODUCT_NOT_FOUND = "PRODUCT_NOT_FOUND";

    /** ロールバックが発生した場合のエラー */
    public static final String ROLLBACK_OCCURRED = "ROLLBACK_OCCURRED";

    private UseCaseErrorCodes() {
    }
}