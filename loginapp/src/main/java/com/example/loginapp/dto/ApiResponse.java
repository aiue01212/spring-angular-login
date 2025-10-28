package com.example.loginapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 成功時のレスポンスを返すDTOクラス
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse {
    /** メッセージ */
    private String message;
}
