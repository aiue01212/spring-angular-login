package com.example.loginapp.domain.service;

import com.example.loginapp.domain.model.User;

/**
 * ユーザ情報に関するドメインサービスインターフェース。
 * ユーザ検索および登録処理を定義する。
 */
public interface UserService {

    /**
     * ユーザ名を指定してユーザ情報を取得する。
     *
     * @param username ユーザ名
     * @return ユーザ情報。存在しない場合は null
     */
    User findUser(String username);

    /**
     * 新規ユーザを登録する。
     *
     * @param user 登録するユーザ情報
     */
    void registerUser(User user);
}