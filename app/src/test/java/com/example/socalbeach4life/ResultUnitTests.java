package com.example.socalbeach4life;

import com.example.socalbeach4life.data.Result;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Result.class)
public class ResultUnitTests {
    @Test
    public void testToString() {
        // given
        Result.Success successResult = new Result.Success("success");
        Result.Error errorResult = new Result.Error(new Exception("error"));
        String expectedSuccess = "Success[data=success]";
        String expectedError = "Error[exception=java.lang.Exception: error]";

        // when
        String actualSuccess = successResult.toString();
        String actualError = errorResult.toString();

        // then
        assertEquals(expectedSuccess, actualSuccess);
        assertEquals(expectedError, actualError);
    }
}
