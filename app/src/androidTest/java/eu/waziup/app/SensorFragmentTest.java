package eu.waziup.app;

import android.content.Intent;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.waziup.app.ui.main.MainActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class SensorFragmentTest {

    @Rule
    public ActivityTestRule rule = new ActivityTestRule(MainActivity.class, true, false);

//    todo has to properly import ActivityTestRule from the espresso
    @Test
    public void onCalculateClicked() {
        rule.launchActivity(new Intent());

//        onView(withId(R.id.btn_calculate)).perform(ViewActions.click());
        onView(withText("Optimized Result")).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }
}
