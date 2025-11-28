package com.example.loginapp.usecase.login;

/**
 * ログイン処理を実行する UseCase の入力境界（InputBoundary）。
 */
public interface LoginInputBoundary {

    /**
     * ログイン処理を実行する。
     *
     * @param input ログインに必要な入力データ
     * @return ログイン結果（成功 / 失敗情報、ユーザ名等）
     */
    LoginOutputData login(LoginInputData input);
}