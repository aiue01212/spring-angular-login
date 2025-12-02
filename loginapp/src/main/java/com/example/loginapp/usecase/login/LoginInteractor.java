package com.example.loginapp.usecase.login;

import com.example.loginapp.domain.model.User;
import com.example.loginapp.domain.service.UserService;
import lombok.RequiredArgsConstructor;
import com.example.loginapp.usecase.constants.UseCaseErrorCodes;

import org.springframework.dao.DataAccessException;

/**
 * ログイン処理を実行する Interactor（UseCase の実装）。
 * 入力（InputData）を受け取り、ドメインサービスを使用して
 * ビジネスロジックを実行し、OutputData を返す。
 */
@RequiredArgsConstructor
public class LoginInteractor implements LoginInputBoundary {

    /** ユーザ情報を扱うドメインサービス */
    private final UserService userService;

    @Override
    public LoginOutputData login(LoginInputData input) {

        User user;

        try {
            user = userService.findUser(input.getUsername());
        } catch (DataAccessException e) {
            return new LoginOutputData(false, null, UseCaseErrorCodes.DB_ERROR);
        }

        if (user == null || !user.isPasswordMatch(input.getPassword())) {
            return new LoginOutputData(false, null, UseCaseErrorCodes.INVALID_CREDENTIALS);
        }

        return new LoginOutputData(true, user.getUsername(), null);
    }
}