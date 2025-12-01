package com.example.loginapp.usecase.product;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * ロールバック確認用更新の入力データ。
 */
@Data
@AllArgsConstructor
public class UpdateTwoProductsInputData {

    /** 1 件目に更新を行う対象商品の ID。 */
    private int id1;

    /** 1 件目の商品に設定する新しい価格。 */
    private BigDecimal price1;

    /** 2 件目に更新を行う対象商品の ID。 */
    private int id2;

    /** 2 件目の商品に設定する新しい価格。 */
    private BigDecimal price2;
}
