package com.example.loginapp.domain.repository;

import com.example.loginapp.domain.model.User;

/**
 * ユーザーに関する永続化操作を定義するリポジトリ（技術非依存）。
 * <p>
 * インフラ層の実際の永続化技術（MyBatis, JPA など）に依存しないため、
 * クリーンアーキテクチャの依存方向（内 → 外）を保つことができる。
 * </p>
 */
public interface UserRepository {

    /**
     * 指定されたユーザ名でユーザ情報を取得する。
     *
     * @param username ユーザ名
     * @return ユーザ情報（存在しない場合 null）
     */
    User findByUsername(String username);

    /**
     * 新しいユーザを永続化する。
     *
     * @param user 保存するユーザ情報
     */
    void insertUser(User user);
}