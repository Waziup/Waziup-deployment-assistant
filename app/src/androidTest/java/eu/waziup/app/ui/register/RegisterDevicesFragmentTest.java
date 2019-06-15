package eu.waziup.app.ui.register;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.waziup.app.R;
import eu.waziup.app.ui.main.MainActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class RegisterDevicesFragmentTest {

    // note: should not be inflating the a fragment in as a rule. should only be activity and through
    // the activity its possible to inflate the fragment
    @Rule
    public ActivityTestRule<MainActivity> rule = new ActivityTestRule<>(MainActivity.class, true, false);

    @Before
    public void setUp() throws Exception {
        // launching the activity
        rule.launchActivity(new Intent());

        rule.getActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.cl_root_view, RegisterSensorFragment.newInstance(), RegisterSensorFragment.TAG)
                .commit();

    }

    @Test
    public void onEditFieldsAndButtonsDisplayed() {
        // sensor_id field
        onView(withId(R.id.register_sensor_id)).check(matches(isDisplayed()));

        // sensor_domain field
        onView(withId(R.id.register_sensor_domain)).check(matches(isDisplayed()));

        // sensor_name field
        onView(withId(R.id.register_sensor_name)).check(matches(isDisplayed()));

        // sensor_visibility field
        onView(withId(R.id.register_sensor_visibility)).check(matches(isDisplayed()));

        // submit button
        onView(withId(R.id.btn_register_submit)).check(matches(isDisplayed()));

        // gps icon for getting current location
        onView(withId(R.id.btn_register_get_current_location)).check(matches(isDisplayed()));

        // toolbar title
        onView(withId(R.id.register_toolbar)).check(matches(hasDescendant(withText(R.string.add_sensor_node))));
    }
}
