package com.example.loginapp.rest.repository;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.example.loginapp.domain.model.User;
import com.example.loginapp.domain.repository.UserRepository;

/**
 * {@link UserRepository} の MyBatis 実装クラス。
 * <p>
 * domain の UserRepository を実装し、SQL を用いた永続化操作を提供する。
 * </p>
 */
@Mapper
public interface MyBatisUserRepository extends UserRepository {

    /**
     * 指定されたユーザ名でユーザ情報を取得する。
     *
     * @param username ユーザ名
     * @return ユーザ情報（存在しない場合 null）
     */
    @Select("SELECT username, password FROM users WHERE username = #{username}")
    @Override
    User findByUsername(String username);

    /**
     * 新しいユーザを永続化する。
     *
     * @param user 保存するユーザ情報
     */
    @Insert("INSERT INTO users (username, password, email) VALUES (#{username}, #{password}")
    @Override
    void insertUser(User user);
}
