package com.example.loginapp.domain.usecase.login;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * ログイン処理の入力データを表す DTO。
 * Controller 層の LoginRequest から変換され、
 * UseCase 層の Interactor に渡される。
 */
@Data
@AllArgsConstructor
public class LoginInputData {

    /** ユーザー名 */
    private String username;

    /** パスワード */
    private String password;
}
