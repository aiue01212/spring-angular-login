package com.example.loginapp.domain.service.impl;

import com.example.loginapp.domain.model.User;
import com.example.loginapp.domain.repository.UserRepository;
import com.example.loginapp.domain.service.UserService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * {@link UserService} の実装クラス。
 * ユーザ情報の取得および登録処理を行う。
 * ユーザ関連の永続化処理はリポジトリを通じて実行される。
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    /** ユーザリポジトリ */
    private final UserRepository userRepository;

    /**
     * ユーザー名からユーザー情報を取得する。
     *
     * @param username ユーザー名
     * @return ユーザー情報
     */
    @Override
    public User findUser(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * ユーザー登録を行う。
     *
     * @param user 登録するユーザー情報
     */
    @Override
    @Transactional
    public void registerUser(User user) {
        userRepository.insertUser(user);
    }
}