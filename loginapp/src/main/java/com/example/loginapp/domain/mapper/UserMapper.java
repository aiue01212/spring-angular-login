package com.example.loginapp.domain.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.example.loginapp.domain.entity.User;

import org.apache.ibatis.annotations.Insert;

/**
 * ユーザー情報のデータベース操作を定義するインターフェース。
 */
@Mapper
public interface UserMapper {

    /**
     * 指定されたユーザー名に対応するユーザーを取得します。
     *
     * @param username 検索対象のユーザー名
     * @return 該当する {@link User} オブジェクト。存在しない場合は {@code null}
     */
    @Select("SELECT * FROM users WHERE username = #{username}")
    User findByUsername(String username);

    /**
     * 新しいユーザーをデータベースに挿入します。
     *
     * @param user 登録するユーザー情報を保持する {@link User} オブジェクト
     */
    @Insert("INSERT INTO users (username, password, email) VALUES (#{username}, #{password}, #{email})")
    void insertUser(User user);
}