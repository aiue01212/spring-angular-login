package com.example.loginapp.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

/**
 * アプリケーション全体で共通的に例外を処理するハンドラークラス。
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /** 内部サーバーエラーのHTTPステータスコード */
    private static final HttpStatus INTERNAL_SERVER_ERROR = HttpStatus.INTERNAL_SERVER_ERROR;

    /**
     * {@link RuntimeException} が発生した際に共通的なエラーレスポンスを返す。
     *
     * @param ex 発生した実行時例外
     * @return エラーメッセージを含むレスポンス
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity
                .status(INTERNAL_SERVER_ERROR)
                .body(Map.of("error", ex.getMessage()));
    }
}
