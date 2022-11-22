package com.example.socalbeach4life;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Context;
import android.util.Log;

import androidx.test.espresso.ViewAction;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import com.example.socalbeach4life.databinding.ActivityRegisterBinding;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class BeachTest {
    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void beachesDisplayed() {
        onView(withText("Southern California Aquatics or SCAQ")).check(matches(isDisplayed()));
        onView(withText("Marina Beach")).check(matches(isDisplayed()));
        onView(withText("Mother's Beach")).check(matches(isDisplayed()));
    }

    @Test
    public void beachDetails() {
        onView(withText("Marina Beach")).perform(click());
        onView(withText("BACK TO LIST")).check(matches(isDisplayed()));
    }

    @Test
    public void drawCircles() {
        onView(withText("Marina Beach")).perform(click());
        onView(withText("1000")).check(matches(isDisplayed()));
        onView(withText("2000")).check(matches(isDisplayed()));
        onView(withText("3000")).check(matches(isDisplayed()));
        onView(withText("1000")).perform(click());
        onView(withText("2000")).perform(click());
        onView(withText("3000")).perform(click());
    }

}