package com.example.loginapp.usecase.constants;

import java.math.BigDecimal;

/**
 * UseCase 層で使用する共通定数クラス。
 */
public final class UseCaseConstants {

    private UseCaseConstants() {
    }

    /** ロールバック用アクション名 */
    public static final String ACTION_UPDATE_PRICE = "UPDATE_PRICE";

    /** iPhone の商品ID */
    public static final int PRODUCT_ID_IPHONE = 1;

    /** Galaxy の商品ID */
    public static final int PRODUCT_ID_GALAXY = 2;

    /** iPhone 更新用価格 */
    public static final BigDecimal PRICE_UPDATE_1 = BigDecimal.valueOf(100);

    /** Galaxy 更新用価格 */
    public static final BigDecimal PRICE_UPDATE_2 = BigDecimal.valueOf(200);
}