package com.example.loginapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * エラー時のレスポンスを返すDTOクラス
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse implements SessionCheckResponse {

    /** エラーメッセージ */
    private String error;
}