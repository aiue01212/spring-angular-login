package com.example.loginapp.rest.handler;

import org.springframework.context.MessageSource;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.example.loginapp.rest.model.ErrorResponse;

import lombok.RequiredArgsConstructor;

import static com.example.loginapp.domain.constants.MessageKeys.*;

import java.util.Locale;

/**
 * アプリケーション全体で共通的に例外を処理するグローバル例外ハンドラークラス。
 */
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    /** メッセージソース */
    private final MessageSource messageSource;

    /** DB例外 */
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorResponse> handleDataAccessException(
            DataAccessException ex, Locale locale) {

        String msg = messageSource.getMessage(ERROR_DATABASE_ACCESS, new Object[] { ex.getMessage() }, locale);
        if (msg == null) {
            msg = "データベースアクセスエラーが発生しました";
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(msg));
    }

    /** セッション関連の例外 */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalStateException(
            IllegalStateException ex, Locale locale) {

        String msg = messageSource.getMessage(ERROR_INTERNAL_SERVER, null, locale);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(msg));
    }

    /** その他の実行時例外 */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(
            RuntimeException ex, Locale locale) {

        String msg = messageSource.getMessage(ERROR_INTERNAL_SERVER, null, locale);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(msg));
    }
}
