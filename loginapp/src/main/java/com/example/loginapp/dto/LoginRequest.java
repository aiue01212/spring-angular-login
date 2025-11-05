package com.example.loginapp.dto;

import lombok.Data;

/**
 * ログインリクエストを表すDTOクラス。
 */
@Data
public class LoginRequest {

    /** ユーザー名 */
    private String username;

    /** パスワード */
    private String password;
}