package com.example.loginapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * エラー時のレスポンスを返すDTOクラス
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    /** エラーメッセージ */
    private String error;
}