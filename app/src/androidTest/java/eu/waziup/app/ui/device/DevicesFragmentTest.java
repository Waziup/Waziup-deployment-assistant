package eu.waziup.app.ui.device;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.waziup.app.R;
import eu.waziup.app.ui.main.MainActivity;

import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class DevicesFragmentTest {

    private DevicesFragment mDevicesFragment;

    @Rule
    public ActivityTestRule<MainActivity> rule =
            new ActivityTestRule<>(MainActivity.class, true, false);


    @Before
    public void setUp() throws Exception {
        // launching the activity
        rule.launchActivity(new Intent());

        rule.getActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.flContent, DevicesFragment.newInstance(), DevicesFragment.TAG)
                .commit();
    }

    @Test
    public void onFabClicked() throws Exception {
//        onView(withId(R.id.sensor_recycler)).check(matches(isDisplayed()));
//        onView(withId(R.id.fab_sensor)).perform(click());
//        getInstrumentation().waitForIdleSync();// for telling the test to wait for a while so that the inflation comes first before searching for a view with its id
//        onView(withId(R.id.fab_sensor)).check(matches(isDisplayed()));
//        mDevicesFragment.getView().findViewById(R.id.fab_sensor)
    }

}
