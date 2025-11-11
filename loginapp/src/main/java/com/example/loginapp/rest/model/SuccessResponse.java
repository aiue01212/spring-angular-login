package com.example.loginapp.rest.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 成功時のレスポンスメッセージを表すDTOクラス。
 */
@Data
@AllArgsConstructor
public class SuccessResponse implements SessionCheckResponse {

    /** メッセージ本文 */
    private String message;
}
