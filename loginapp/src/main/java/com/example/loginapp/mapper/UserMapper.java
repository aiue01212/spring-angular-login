package com.example.loginapp.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Insert;

import com.example.loginapp.entity.User;

@Mapper
public interface UserMapper {

    @Select("SELECT * FROM users WHERE username = #{username}")
    User findByUsername(String username);

    @Insert("INSERT INTO users (username, password, email) VALUES (#{username}, #{password}, #{email})")
    void insertUser(User user);
}