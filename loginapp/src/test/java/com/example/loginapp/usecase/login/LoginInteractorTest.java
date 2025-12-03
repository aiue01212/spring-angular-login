package com.example.loginapp.usecase.login;

import com.example.loginapp.domain.model.User;
import com.example.loginapp.domain.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static com.example.loginapp.usecase.constants.UseCaseErrorCodes.*;
import static com.example.loginapp.usecase.constants.Constants.*;

class LoginInteractorTest {

    /** ユーザー情報の取得を担当するドメインサービス */
    private UserService userService;

    /** ログイン処理を行うユースケースの実装クラス */
    private LoginInteractor loginInteractor;

    /**
     * 各テストメソッド実行前に共通のセットアップを行う。
     */
    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        loginInteractor = new LoginInteractor(userService);
    }

    /**
     * 正常にログインできる場合の動作を検証する。
     */
    @Test
    void testLoginSuccess() {
        User user = new User();
        user.setUsername(VALID_USERNAME);
        user.setPassword(VALID_PASSWORD);

        when(userService.findUser(VALID_USERNAME)).thenReturn(user);

        LoginInputData input = new LoginInputData(VALID_USERNAME, VALID_PASSWORD);
        LoginOutputData output = loginInteractor.login(input);

        assertTrue(output.isSuccess());
        assertEquals(VALID_USERNAME, output.getUsername());
        assertNull(output.getErrorCode());
    }

    /**
     * パスワードが誤っている場合、認証エラーが返されることを検証する。
     */
    @Test
    void testLoginInvalidPassword() {
        User user = new User();
        user.setUsername(VALID_USERNAME);
        user.setPassword(VALID_PASSWORD);

        when(userService.findUser(VALID_USERNAME)).thenReturn(user);

        LoginInputData input = new LoginInputData(VALID_USERNAME, INVALID_PASSWORD);
        LoginOutputData output = loginInteractor.login(input);

        assertFalse(output.isSuccess());
        assertEquals(INVALID_CREDENTIALS, output.getErrorCode());
    }

    /**
     * ユーザーが存在しない場合、認証エラーが返されることを検証する。
     */
    @Test
    void testLoginUserNotFound() {
        when(userService.findUser(NON_EXISTENT_USERNAME)).thenReturn(null);

        LoginInputData input = new LoginInputData(NON_EXISTENT_USERNAME, VALID_PASSWORD);
        LoginOutputData output = loginInteractor.login(input);

        assertFalse(output.isSuccess());
        assertEquals(INVALID_CREDENTIALS, output.getErrorCode());
    }

    /**
     * データアクセス例外が発生した場合、DBエラーコードが返されることを検証する。
     */
    @Test
    void testLoginDataAccessException() {
        when(userService.findUser(VALID_USERNAME)).thenThrow(new DataAccessException(DB_ERROR) {
        });

        LoginInputData input = new LoginInputData(VALID_USERNAME, VALID_PASSWORD);
        LoginOutputData output = loginInteractor.login(input);

        assertFalse(output.isSuccess());
        assertEquals(DB_ERROR, output.getErrorCode());
    }
}
