package com.example.loginapp.rest.constants;

import java.math.BigDecimal;

public class UpdateConstants {

    private UpdateConstants() {
    }

    /** 更新対象商品1のID（iPhone） */
    public static final int PRODUCT_ID_1 = 1;

    /** 更新対象商品2のID（Galaxy） */
    public static final int PRODUCT_ID_2 = 2;

    /** 商品1の更新後価格 */
    public static final BigDecimal PRICE_UPDATE_1 = BigDecimal.valueOf(100);

    /** 商品2の更新後価格 */
    public static final BigDecimal PRICE_UPDATE_2 = BigDecimal.valueOf(200);

}
