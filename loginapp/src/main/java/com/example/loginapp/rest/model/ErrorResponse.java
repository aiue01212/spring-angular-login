package com.example.loginapp.rest.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * エラー時のレスポンスを返すDTOクラス
 */
@Data
@AllArgsConstructor
public class ErrorResponse implements SessionCheckResponse {

    /** エラーメッセージ */
    private String error;
}