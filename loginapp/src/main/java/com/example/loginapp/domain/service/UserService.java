package com.example.loginapp.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.loginapp.domain.entity.User;
import com.example.loginapp.domain.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    /**
     * ユーザー名からユーザー情報を取得する。
     *
     * @param username ユーザー名
     * @return ユーザー情報
     */
    public User findUser(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * ユーザー登録を行う。
     *
     * @param user 登録するユーザー情報
     */
    @Transactional
    public void registerUser(User user) {
        userRepository.insertUser(user);
    }
}
