package com.example.socalbeach4life;

import com.example.socalbeach4life.yelp.YelpAsyncTask;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(YelpAsyncTask.class)
public class YelpAsyncTaskUnitTests {
    @Test
    public void testBuildYelpURL() {
        // given
        YelpAsyncTask yelpAsyncTask = new YelpAsyncTask();
        String[] strings = {"businesses/search", "valueOne", "key one", "valueTwo", "key two"};
        String expectedURL = "https://api.yelp.com/v3/businesses/search?valueOne=key%20one&valueTwo=key%20two";

        // when
        String actualURL = yelpAsyncTask.buildYelpURL(strings);

        // then
        assertEquals(expectedURL, actualURL);
    }
}
