package com.example.loginapp.domain.entity;

import jakarta.persistence.*;
import lombok.Data;

/**
 * ユーザ情報を管理するエンティティ。
 * usersテーブルに対応します。
 */
@Entity
@Table(name = "users")
@Data
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
    @Column(nullable = false, unique = true)
    private String username;

    /**
     * パスワード（必須）
     */
    @Column(nullable = false)
    private String password;
}