package com.example.loginapp.annotation;

import java.lang.annotation.*;

/**
 * セッションログインチェックが必要なメソッドに付与するアノテーション。
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SessionRequired {
}
