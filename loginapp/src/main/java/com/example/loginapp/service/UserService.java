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

    public User findUser(String username) {
        return userMapper.findByUsername(username);
    }

    @Transactional
    public void registerUser(User user) {
        userMapper.insertUser(user);
    }
}
