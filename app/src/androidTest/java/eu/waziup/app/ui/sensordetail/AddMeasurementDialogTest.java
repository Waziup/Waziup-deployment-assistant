package eu.waziup.app.ui.sensordetail;

import android.content.Intent;
import android.support.test.filters.SmallTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.waziup.app.R;
import eu.waziup.app.data.network.model.sensor.Sensor;
import eu.waziup.app.ui.main.MainActivity;
import eu.waziup.app.ui.sensor.SensorFragment;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.isDialog;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class AddMeasurementDialogTest {

    DetailSensorFragment mDetailSensorFragment;

    @Rule
    public ActivityTestRule<MainActivity> rule = new ActivityTestRule<>(MainActivity.class, true, false);

    @Before
    public void setUp() throws Exception {
        // launching the activity
        rule.launchActivity(new Intent());

        // for inflating the fragment - with mock data
        mDetailSensorFragment = DetailSensorFragment.newInstance(new Sensor("IST-AFRICA-2018_Sensor6", "",
                "waziup", "public"), SensorFragment.TAG);

        rule.getActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.cl_root_view, mDetailSensorFragment, DetailSensorFragment.TAG)
                .commit();
    }

    @Test
    public void onButtonsAndTextViewsDisplayed() throws Exception {

        // add measurement button
//        onView(withId(R.id.detail_sensor_add_measurement)).check(matches(isDisplayed()));
        // unDeploy button
//        onView(withId(R.id.btn_undeploy)).check(matches(isDisplayed()));
        // deploy button
//        onView(withId(R.id.btn_deploy)).check(matches(isDisplayed()));
        // sensor date
        onView(withId(R.id.detail_sensor_date)).check(matches(isDisplayed()));
        // toolbar title
        onView(withId(R.id.toolbar_title)).check(matches(isDisplayed()));
    }

//    @Test
//    public void onAddButtonClicked() throws Exception {
//        // performs add button click
//        onView(withId(R.id.detail_sensor_add_measurement)).perform(click());
//
//        onView(withText(R.string.edit_measurement))
//                .inRoot(isDialog()) // <--- checking if the inflated view is a dialog with the textView
//                .check(matches(isDisplayed()));
//
//        // tells the ide to wait for the button being clicked before checking
//        getInstrumentation().waitForIdleSync();
//        // check for the title being displayed - Edit Measurement
//        onView(withId(R.id.dialog_measurement_title)).check(matches(isDisplayed()));
//        // check for the id field being displayed
//        onView(withId(R.id.dialog_measurement_id)).check(matches(isDisplayed()));
//        // check for the name field being displayed
//        onView(withId(R.id.dialog_measurement_name)).check(matches(isDisplayed()));
//        // check for the quantity_kind being displayed
//        onView(withId(R.id.dialog_measurement_quantity_kind)).check(matches(isDisplayed()));
//        // check for the unit field being displayed
//        onView(withId(R.id.dialog_measurement_unit)).check(matches(isDisplayed()));
//    }

}
