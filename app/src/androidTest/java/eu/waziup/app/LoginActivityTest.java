package eu.waziup.app;

import android.content.Intent;
import android.support.test.filters.SmallTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.waziup.app.ui.login.LoginActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class LoginActivityTest {

    @Rule
    public ActivityTestRule<LoginActivity> rule = new ActivityTestRule<>(LoginActivity.class, true, false);

    /**
     * For checking whether the editTexts and the login button have been displayed or not
     */
    @Test
    public void onEditTestAndButtonDisplayed(){
        // launching the activity
        rule.launchActivity(new Intent());

        // username field
        onView(withId(R.id.et_username)).check(matches(isDisplayed()));
        // password field
        onView(withId(R.id.et_password)).check(matches(isDisplayed()));
        // login button
        onView(withId(R.id.btn_login)).check(matches(isDisplayed()));
    }

    /**
     * For performing actions like typing in the username and password and then later clicking the
     * login button
     */
    @Test
    public void onLoginButtonClicked() {
        // launching the activity
        rule.launchActivity(new Intent());

        // performing action for user login - typing username and password and clicking login button
        onView(withId(R.id.et_username)).perform(typeText("cdupont"), closeSoftKeyboard());
        onView(withId(R.id.et_password)).perform(typeText("password"), closeSoftKeyboard());
        onView(withId(R.id.btn_login)).perform(click());
    }
}
