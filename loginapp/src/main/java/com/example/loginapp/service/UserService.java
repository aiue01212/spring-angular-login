package com.example.loginapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.loginapp.entity.User;
import com.example.loginapp.mapper.UserMapper;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    /**
     * ユーザー名からユーザー情報を取得する。
     *
     * @param username ユーザー名
     * @return ユーザー情報
     */
    public User findUser(String username) {
        return userMapper.findByUsername(username);
    }

    /**
     * ユーザー登録を行う。
     *
     * @param user 登録するユーザー情報
     */
    @Transactional
    public void registerUser(User user) {
        userMapper.insertUser(user);
    }
}
