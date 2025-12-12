package com.example.loginapp.domain.usecase.product;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ロールバック確認用更新の入力データ。
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateTwoProductsInputData {

    /**
     * 1つ目に更新する商品の ID。
     */
    private int productId1;

    /**
     * 1つ目の商品に設定する新しい価格。
     */
    private BigDecimal newPrice1;

    /**
     * 2つ目に更新する商品の ID。
     */
    private int productId2;

    /**
     * 2つ目の商品に設定する新しい価格。
     */
    private BigDecimal newPrice2;
}
