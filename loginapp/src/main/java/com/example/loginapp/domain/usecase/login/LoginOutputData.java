package com.example.loginapp.domain.usecase.login;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * ログイン処理の結果を表す DTO。
 * UseCase 実行後、Controller や Presenter はこの DTO を利用して
 * レスポンスモデルに変換する。
 */
@Data
@AllArgsConstructor
public class LoginOutputData {

    /** ログイン成功かどうか */
    private final boolean success;

    /** 成功時のユーザー名（失敗時は null） */
    private final String username;

    /** エラーコード（成功時は null） */
    private final String errorCode;
}