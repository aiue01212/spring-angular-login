package com.example.loginapp.dto;

import lombok.Data;

/**
 * ログインリクエストを表すDTOクラス。
 * <p>
 * フロントエンドから送信されるユーザー名とパスワードを保持する。
 * </p>
 */
@Data
public class LoginRequest {

    /** ユーザー名 */
    private String username;

    /** パスワード */
    private String password;
}