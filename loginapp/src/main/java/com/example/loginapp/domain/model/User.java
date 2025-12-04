package com.example.loginapp.domain.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * ユーザ情報を管理するエンティティ。
 * usersテーブルに対応します。
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class User {

    /**
     * ユーザID（自動採番）
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * ユーザ名（ユニーク、必須）
     */
    @NonNull
    @Column(nullable = false, unique = true)
    private String username;

    /**
     * パスワード（必須）
     */
    @NonNull
    @Column(nullable = false)
    private String password;

    /**
     * パスワード一致判定
     */
    public boolean isPasswordMatch(String rawPassword) {
        return password != null && password.equals(rawPassword);
    }
}