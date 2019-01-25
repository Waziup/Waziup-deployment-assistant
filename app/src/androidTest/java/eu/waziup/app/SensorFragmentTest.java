package eu.waziup.app;

import android.content.Intent;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.waziup.app.ui.main.MainActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class SensorFragmentTest {

    @Rule
    public ActivityTestRule<MainActivity> activityTestRule =
            new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setUpFragment() {
        activityTestRule.getActivity().getSupportFragmentManager().beginTransaction();
    }

    @Test
    public void onCalculateClicked() {
//        rule.launchActivity(new Intent());

//        onView(withId(R.id.btn_calculate)).perform(ViewActions.click());
        onView(withId(R.id.fab_sensor)).check(matches(isDisplayed()));
    }
}
