package com.example.loginapp.domain.usecase.constants;

import java.math.BigDecimal;

/**
 * 単体テスト用の定数クラス。
 */
public final class Constants {

    private Constants() {
    }

    /** 有効なユーザー名 */
    public static final String VALID_USERNAME = "user";

    /** 有効なパスワード */
    public static final String VALID_PASSWORD = "pass";

    /** 無効なパスワード */
    public static final String INVALID_PASSWORD = "wrongpass";

    /** 存在しないユーザー名 */
    public static final String NON_EXISTENT_USERNAME = "nouser";

    /** iPhone の商品ID */
    public static final int PRODUCT_ID_IPHONE = 1;

    /** Galaxy の商品ID */
    public static final int PRODUCT_ID_GALAXY = 2;

    /** 存在しない商品ID */
    public static final int PRODUCT_ID_NOT_FOUND = 999;

    /** iPhone の価格（double） */
    public static final double PRICE_IPHONE = 120000.00;

    /** Galaxy の価格（double） */
    public static final double PRICE_GALAXY = 98000.00;

    /** iPhone の価格（BigDecimal） */
    public static final BigDecimal PRICE_IPHONE_BD = BigDecimal.valueOf(PRICE_IPHONE);

    /** Galaxy の価格（BigDecimal） */
    public static final BigDecimal PRICE_GALAXY_BD = BigDecimal.valueOf(PRICE_GALAXY);

    /** 更新用価格1（BigDecimal） */
    public static final BigDecimal PRICE_UPDATE_1 = BigDecimal.valueOf(100);

    /** 更新用価格2（BigDecimal） */
    public static final BigDecimal PRICE_UPDATE_2 = BigDecimal.valueOf(200);

    /** iPhone の商品名 */
    public static final String PRODUCT_NAME_IPHONE = "iPhone";

    /** Galaxy の商品名 */
    public static final String PRODUCT_NAME_GALAXY = "Galaxy";

    /** BigDecimal 比較で値が等しい場合の戻り値 */
    public static final int BIGDECIMAL_EQUAL = 0;

    /** 商品リストにおける iPhone のインデックス */
    public static final int INDEX_IPHONE = 0;

    /** 商品リストにおける Galaxy のインデックス */
    public static final int INDEX_GALAXY = 1;

    /** 商品リストの総数 */
    public static final int TOTAL_PRODUCTS = 2;
}
