package com.example.loginapp.domain.usecase.constants;

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

    /** 商品 ID 指定時に該当商品が見つからない場合のエラー */
    public static final String ERROR_PRODUCT_NOT_FOUND_ID = "ERROR_PRODUCT_NOT_FOUND_ID";

    /** トランザクションロールバックが発生した場合のエラー */
    public static final String ROLLBACK_ERROR = "ROLLBACK_ERROR";

    private UseCaseErrorCodes() {
    }
}