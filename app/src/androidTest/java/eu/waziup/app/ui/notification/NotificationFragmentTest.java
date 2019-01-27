package eu.waziup.app.ui.notification;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import eu.waziup.app.R;
import eu.waziup.app.ui.main.MainActivity;
import eu.waziup.app.ui.sensor.SensorFragment;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.*;

public class NotificationFragmentTest {


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
                .replace(R.id.flContent, NotificationFragment.newInstance(), NotificationFragment.TAG)
                .commit();
    }

    @Test
    public void recyclerViewItemsDisplayed() throws Exception {
        // check for existence of the recyclerView
        onView(withId(R.id.notification_recycler)).check(matches(isDisplayed()));

        // check for notification with text sensor_name: Sensor2-ea0541de1ab7132a1d45b85f9b2139f5
        onView(withId(R.id.notification_recycler)).check(matches(hasDescendant(withText("Sensor2-ea0541de1ab7132a1d45b85f9b2139f5"))));
        // check for notification with text sensor_name: ISPACE_Sensor5
        onView(withId(R.id.notification_recycler)).check(matches(hasDescendant(withText("ISPACE_Sensor5"))));

    }

}