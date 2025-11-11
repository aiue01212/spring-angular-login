package com.example.loginapp.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.example.loginapp.rest.model.ErrorResponse;

/**
 * アプリケーション全体で共通的に例外を処理するグローバル例外ハンドラークラス。
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * {@link RuntimeException} が発生した場合に共通的なエラーレスポンスを返す。
     *
     * @param ex 発生した実行時例外
     * @return エラーメッセージを含む {@link ResponseEntity<ErrorResponse>}
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse);
    }
}
