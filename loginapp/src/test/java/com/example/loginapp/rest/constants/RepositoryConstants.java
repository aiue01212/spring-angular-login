package com.example.loginapp.rest.constants;

import java.math.BigDecimal;
import java.util.List;
import com.example.loginapp.domain.model.Product;
import com.example.loginapp.domain.model.User;

public class RepositoryConstants {
    private RepositoryConstants() {
    }

    /** 空リスト判定用 */
    public static final int EMPTY_LIST_SIZE = 0;

    /** List の最初の要素 */
    public static final int FIRST_PRODUCT_INDEX = 0;

    /** 商品ID */
    public static final int PRODUCT_ID_1 = 1;
    public static final int PRODUCT_ID_2 = 2;

    /** 引数インデックス: 商品ID */
    public static final int ARG_INDEX_ID = 0;

    /** 引数インデックス: 商品価格 */
    public static final int ARG_INDEX_PRICE = 1;

    /** 引数インデックス: User オブジェクト */
    public static final int ARG_INDEX_USER = 0;

    /** 商品名 */
    public static final String PRODUCT_NAME_1 = "Test Product 1";
    public static final String PRODUCT_NAME_2 = "Test Product 2";

    /** 商品価格 */
    public static final BigDecimal PRODUCT_PRICE_1 = BigDecimal.valueOf(1000);
    public static final BigDecimal PRODUCT_PRICE_2 = BigDecimal.valueOf(2000);
    public static final BigDecimal PRODUCT_NEW_PRICE = BigDecimal.valueOf(9999);

    /** 商品更新用引数 */
    public static final int UPDATE_PRICE_PRODUCT_ID = PRODUCT_ID_1;
    public static final BigDecimal UPDATE_PRICE_NEW_PRICE = PRODUCT_NEW_PRICE;

    /** ログ挿入用アクション */
    public static final String TEST_ACTION = "TEST_ACTION";

    /** 商品リスト */
    public static final List<Product> PRODUCT_LIST = List.of(
            new Product(PRODUCT_ID_1, PRODUCT_NAME_1, PRODUCT_PRICE_1),
            new Product(PRODUCT_ID_2, PRODUCT_NAME_2, PRODUCT_PRICE_2));

    /** ユーザ情報 */
    public static final String USERNAME_1 = "user1";
    public static final String PASSWORD_1 = "pass1";
    public static final User USER_1 = new User(USERNAME_1, PASSWORD_1);

    public static final String USERNAME_2 = "user2";
    public static final String PASSWORD_2 = "pass2";
    public static final User USER_2 = new User(USERNAME_2, PASSWORD_2);
}
