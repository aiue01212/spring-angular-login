package com.example.loginapp.usecase.constants;

import java.math.BigDecimal;

/**
 * 単体テスト用の定数クラス。
 */
public final class Constants {

    private Constants() {
    }

    public static final String VALID_USERNAME = "user";
    public static final String VALID_PASSWORD = "pass";
    public static final String INVALID_PASSWORD = "wrongpass";
    public static final String NON_EXISTENT_USERNAME = "nouser";

    public static final int PRODUCT_ID_IPHONE = 1;
    public static final int PRODUCT_ID_GALAXY = 2;
    public static final int PRODUCT_ID_NOT_FOUND = 999;

    public static final double PRICE_IPHONE = 120000.00;
    public static final double PRICE_GALAXY = 98000.00;

    public static final BigDecimal PRICE_IPHONE_BD = BigDecimal.valueOf(PRICE_IPHONE);
    public static final BigDecimal PRICE_GALAXY_BD = BigDecimal.valueOf(PRICE_GALAXY);

    public static final BigDecimal PRICE_UPDATE_1 = BigDecimal.valueOf(100);
    public static final BigDecimal PRICE_UPDATE_2 = BigDecimal.valueOf(200);

    public static final String PRODUCT_NAME_IPHONE = "iPhone";
    public static final String PRODUCT_NAME_GALAXY = "Galaxy";

    public static final int BIGDECIMAL_EQUAL = 0;
    public static final int INDEX_IPHONE = 0;
    public static final int INDEX_GALAXY = 1;
    public static final int TOTAL_PRODUCTS = 2;
}
