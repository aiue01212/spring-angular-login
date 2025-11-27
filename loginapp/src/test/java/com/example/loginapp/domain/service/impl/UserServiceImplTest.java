package com.example.loginapp.domain.service.impl;

import com.example.loginapp.domain.model.User;
import com.example.loginapp.domain.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * {@link UserServiceImpl} の単体テストクラス。
 * <p>
 * ユーザー取得およびユーザー登録の正常系・例外発生時の動作を検証する。
 * </p>
 */
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    /** モック化された UserRepository */
    @Mock
    private UserRepository userRepository;

    /** テスト対象の UserServiceImpl（依存は自動注入） */
    @InjectMocks
    private UserServiceImpl userService;

    /** テストで使用する共通定数群 */
    private static final String USERNAME_TEST = "test";
    private static final String PASSWORD_TEST = "pass";

    private static final String USERNAME_UNKNOWN = "unknown";
    private static final String USERNAME_NEW = "newuser";
    private static final String USERNAME_ERROR = "errorUser";

    private static final String DB_ERROR_MESSAGE = "DB error";

    /**
     * テストや処理中に使用されるメッセージ定数をまとめた定義。
     */
    private static final int ONCE = 1;

    /**
     * findUser(): ユーザが存在する場合、User オブジェクトを返すことを確認。
     */
    @Test
    void findUser_ReturnsUser() {
        User mockUser = new User();
        mockUser.setUsername(USERNAME_TEST);
        mockUser.setPassword(PASSWORD_TEST);

        when(userRepository.findByUsername(USERNAME_TEST)).thenReturn(mockUser);

        User result = userService.findUser(USERNAME_TEST);

        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo(USERNAME_TEST);
        verify(userRepository, times(ONCE)).findByUsername(eq(USERNAME_TEST));
    }

    /**
     * findUser(): ユーザが存在しない場合 null を返すことを確認。
     */
    @Test
    void findUser_ReturnsNull_WhenNotFound() {
        when(userRepository.findByUsername(USERNAME_UNKNOWN)).thenReturn(null);

        User result = userService.findUser(USERNAME_UNKNOWN);

        assertThat(result).isNull();
    }

    /**
     * registerUser(): リポジトリの insertUser() が 1 回呼ばれることを確認（正常系）。
     */
    @Test
    void registerUser_CallsRepositoryOnce() {
        User user = new User();
        user.setUsername(USERNAME_NEW);

        doNothing().when(userRepository).insertUser(any(User.class));

        userService.registerUser(user);

        verify(userRepository, times(ONCE)).insertUser(eq(user));
    }

    /**
     * registerUser(): insertUser() が例外を投げた場合、サービス側でも例外がスローされることを確認。
     */
    @Test
    void registerUser_ThrowsException_WhenRepositoryFails() {
        User user = new User();
        user.setUsername(USERNAME_ERROR);

        doThrow(new DataAccessException(DB_ERROR_MESSAGE) {
        }).when(userRepository).insertUser(any(User.class));

        assertThrows(DataAccessException.class, () -> userService.registerUser(user));
        verify(userRepository, times(ONCE)).insertUser(eq(user));
    }
}
