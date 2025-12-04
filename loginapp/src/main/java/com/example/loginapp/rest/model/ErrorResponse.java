package com.example.loginapp.rest.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * API エラー応答を表すレスポンス DTO
 */
@Data
@AllArgsConstructor
public class ErrorResponse implements SessionCheckResponse {

    /** エラーメッセージ */
    private String error;
}