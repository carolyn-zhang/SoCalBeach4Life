package com.example.socalbeach4life;

import com.example.socalbeach4life.data.LoginDataSource;
import com.example.socalbeach4life.data.LoginRepository;
import com.example.socalbeach4life.data.Result;
import com.example.socalbeach4life.data.model.LoggedInUser;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(LoginRepository.class)
public class LoginRepositoryUnitTests {

    @Test
    @PrepareForTest(Result.Success.class)
    public void testLogin() {
        // given
        LoginDataSource mockLoginDataSource = mock(LoginDataSource.class);
        LoginRepository loginRepository = new LoginRepository(mockLoginDataSource);
        String testUsername = "test username";
        String testPassword = "test password";

        Result<LoggedInUser> expectedResult = mock(Result.Success.class);
        PowerMockito.when(mockLoginDataSource.login(testUsername, testPassword)).thenReturn(expectedResult);

        // when
        loginRepository.login(testUsername, testPassword);

        // then
        verify((Result.Success<LoggedInUser>) expectedResult, times(1)).getData();
    }
}
