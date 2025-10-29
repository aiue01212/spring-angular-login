package com.example.loginapp.aop;

import java.lang.annotation.*;

/**
 * セッションログインチェックが必要なメソッドに付与するアノテーション。
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SessionRequired {
}
