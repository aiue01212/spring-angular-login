package com.example.loginapp.usecase.product;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * ロールバック確認用更新処理の出力 DTO。
 */
@Data
@AllArgsConstructor
public class UpdateTwoProductsOutputData {

    /** 成功したかどうか */
    private final boolean success;

    /** エラー時のメッセージ */
    private final String errorMessage;
}
