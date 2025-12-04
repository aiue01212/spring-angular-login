package com.example.loginapp.rest.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.ArgumentMatchers.any;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.loginapp.domain.model.User;
import com.example.loginapp.rest.constants.RepositoryConstants;

/**
 * MyBatisUserRepository の単体テスト。
 */
@ExtendWith(MockitoExtension.class)
public class MyBatisUserRepositoryTest {

    @Mock
    private MyBatisUserRepository userRepository;

    /**
     * ユーザ名による検索テスト。
     */
    @Test
    void testFindByUsername() {
        when(userRepository.findByUsername(RepositoryConstants.USERNAME_1))
                .thenReturn(RepositoryConstants.USER_1);

        User user = userRepository.findByUsername(RepositoryConstants.USERNAME_1);
        assertNotNull(user);
        assertEquals(RepositoryConstants.USERNAME_1, user.getUsername());
    }

    /**
     * ユーザ挿入テスト。
     */
    @Test
    void testInsertUser() {
        List<User> userList = new ArrayList<>();

        when(userRepository.findByUsername(RepositoryConstants.USERNAME_2))
                .thenAnswer(invocation -> userList.stream()
                        .filter(u -> u.getUsername().equals(RepositoryConstants.USERNAME_2))
                        .findFirst()
                        .orElse(null));

        doAnswer(invocation -> {
            User newUser = invocation.getArgument(RepositoryConstants.ARG_INDEX_USER);
            userList.add(newUser);
            return null;
        }).when(userRepository).insertUser(any(User.class));

        userRepository.insertUser(RepositoryConstants.USER_2);
        User inserted = userRepository.findByUsername(RepositoryConstants.USERNAME_2);

        assertNotNull(inserted);
        assertEquals(RepositoryConstants.USERNAME_2, inserted.getUsername());
    }
}