package com.example.socalbeach4life;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
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
public class LoginTest {

    @Rule
    public ActivityScenarioRule<Login> activityRule =
            new ActivityScenarioRule<>(Login.class);

    @Test
    public void loginPageDisplayed() {
        onView(withId(R.id.login_email)).check(matches(isDisplayed()));    }

    @Test
    public void loginSuccessful() {
        onView(withId(R.id.login_email)).perform(typeText("achueh@usc.edu"));
        onView(withId(R.id.login_password)).perform(typeText("123"));
        closeSoftKeyboard();
        Intents.init();
        onView(withId(R.id.login_button)).perform(click());
        intended(hasComponent(MainActivity.class.getName()));
    }

    @Test
    public void loginFail() {
        onView(withId(R.id.login_email)).perform(typeText("achueh@usc.edu"));
        onView(withId(R.id.login_password)).perform(typeText("x"));
        closeSoftKeyboard();
        onView(withId(R.id.login_button)).perform(click());
        onView(withId(R.id.login_email)).check(matches(isDisplayed()));
    }

    @Test
    public void logoutSuccessful() {
        onView(withId(R.id.login_email)).perform(typeText("achueh@usc.edu"));
        onView(withId(R.id.login_password)).perform(typeText("123"));
        closeSoftKeyboard();
        onView(withId(R.id.login_button)).perform(click());
        onView(withId(R.id.profile)).perform(click());
        onView(withId(R.id.logout_button)).perform(click());
        onView(withId(R.id.login_email)).check(matches(isDisplayed()));
    }
}