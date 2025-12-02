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

    private UserService userService;
    private LoginInteractor loginInteractor;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        loginInteractor = new LoginInteractor(userService);
    }

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

    @Test
    void testLoginUserNotFound() {
        when(userService.findUser(NON_EXISTENT_USERNAME)).thenReturn(null);

        LoginInputData input = new LoginInputData(NON_EXISTENT_USERNAME, VALID_PASSWORD);
        LoginOutputData output = loginInteractor.login(input);

        assertFalse(output.isSuccess());
        assertEquals(INVALID_CREDENTIALS, output.getErrorCode());
    }

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
