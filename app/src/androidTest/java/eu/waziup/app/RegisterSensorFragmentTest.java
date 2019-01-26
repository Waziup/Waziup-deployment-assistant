package eu.waziup.app;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.waziup.app.ui.main.MainActivity;
import eu.waziup.app.ui.register.RegisterSensorFragment;

@RunWith(AndroidJUnit4.class)
public class RegisterSensorFragmentTest {

    // note: should not be inflating the a fragment in as a rule. should only be activity and through
    // the activity its possible to inflate the fragment
    @Rule
    public ActivityTestRule<MainActivity> rule = new ActivityTestRule<>(MainActivity.class, true, false);

    // todo has to properly import ActivityTestRule from the espresso
    @Test
    public void onCalculateClicked() {
//        rule.launchActivity(new Intent());
//
//        Espresso.onView(ViewMatchers.withId(R.id.btn_calculate)).perform(ViewActions.click());
//        Espresso.onView(ViewMatchers.withText("Optimized Result")).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }
}
