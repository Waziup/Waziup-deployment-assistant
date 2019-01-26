package eu.waziup.app.ui.sensor;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.waziup.app.R;
import eu.waziup.app.ui.main.MainActivity;
import eu.waziup.app.ui.sensor.SensorFragment;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class SensorFragmentTest {

    private SensorFragment mSensorFragment;

    @Rule
    public ActivityTestRule<MainActivity> activityTestRule =
            new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setUpFragment() {
        // for inflating the fragment
        mSensorFragment = SensorFragment.newInstance();
        activityTestRule.getActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.flContent, mSensorFragment, SensorFragment.TAG)
                .commit();
    }

    @Test
    public void onCalculateClicked() {
//        onView(withId(R.id.btn_calculate)).perform(ViewActions.click());
        getInstrumentation().waitForIdleSync();// for telling the test to wait for a while so that the inflation comes first before searching for a view with its id
        onView(withId(R.id.fab_sensor)).check(matches(isDisplayed()));
//        mSensorFragment.getView().findViewById(R.id.fab_sensor)
    }

    /**
     * Instrumentation testing for recyclerView
     */
    @Test
    public void itemInMiddleOfList_hasSpecialText() {
        // First, scroll to the view holder using the isInTheMiddle() matcher.
//        onView(withId(R.id.sensor_recycler))
//                .perform(RecyclerViewActions.scrollToHolder(isInTheMiddle()));
//
//        // Check that the item has the special text.
//        String middleElementText =
//                activityTestRule.getActivity().getResources()
//                        .getString(R.string.middle);
//        onView(withText(middleElementText)).check(matches(isDisplayed()));
    }
}
