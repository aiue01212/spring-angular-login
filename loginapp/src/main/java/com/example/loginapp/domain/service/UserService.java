package com.example.loginapp.domain.service;

import com.example.loginapp.domain.model.User;

/**
 * ユーザー情報に関するドメインサービスのインターフェース。
 */
public interface UserService {

    /**
     * 指定されたユーザ名でユーザ情報を取得する。
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