package eu.waziup.app.ui.detail;

import android.content.Intent;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.filters.SmallTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import eu.waziup.app.R;
import eu.waziup.app.data.network.model.sensor.LastValue;
import eu.waziup.app.data.network.model.sensor.Measurement;
import eu.waziup.app.data.network.model.sensor.Sensor;
import eu.waziup.app.ui.login.LoginActivity;
import eu.waziup.app.ui.main.MainActivity;
import eu.waziup.app.ui.sensor.SensorFragment;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.isDialog;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class EditMeasurementDialogTest {

    DetailSensorFragment mDetailSensorFragment;

    @Rule
    public ActivityTestRule<MainActivity> rule = new ActivityTestRule<>(MainActivity.class, true, false);

    @Before
    public void setUp() throws Exception {
        // launching the activity
        rule.launchActivity(new Intent());

        Sensor mSensor = new Sensor("IST-AFRICA-2018_Sensor6", "","waziup", "public");

        Measurement mMeasurement = new Measurement();
        mMeasurement.setId("TC");
        // lastValue
        LastValue mLastValue = new LastValue();
        mLastValue.setValue("24.26");
        mLastValue.setDateReceived("2018-05-10T10:30:25.00Z");
        mLastValue.setTimestamp("2018-05-10T12:30:23+02:00");

        mMeasurement.setLastValue(mLastValue);
        mMeasurement.setId("TC");
        ArrayList<Measurement> measurements = new ArrayList<>();
        measurements.add(mMeasurement);

        //passing mock measurement data to mock sensor data
        mSensor.setMeasurements(measurements);
        // for inflating the fragment - with mock data
        mDetailSensorFragment = DetailSensorFragment.newInstance(mSensor, SensorFragment.TAG);

        // inflating DetailSensorFragment
        rule.getActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.cl_root_view, mDetailSensorFragment, DetailSensorFragment.TAG)
                .commit();
    }

    @Test
    public void onButtonsAndTextViewsDisplayed() throws Exception {
        // add measurement button
        onView(withId(R.id.detail_sensor_add_measurement)).check(matches(isDisplayed()));

        // -- below are views displayed on DetailSensorFragment fragment
        // unDeploy button
        onView(withId(R.id.btn_undeploy)).check(matches(isDisplayed()));            //(1)
        // deploy button
        onView(withId(R.id.btn_deploy)).check(matches(isDisplayed()));              //(2)
        // sensor date
        onView(withId(R.id.detail_sensor_date)).check(matches(isDisplayed()));      //(3)
        // toolbar title
        onView(withId(R.id.toolbar_title)).check(matches(isDisplayed()));           //(4)
    }

    @Test
    public void onEditButtonClicked() throws Exception {
        // performs click on one of recyclerView items
//        onView(withId(R.id.measurement_recycler)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        // perform click on
        onView(withId(R.id.measurement_recycler)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, MyViewAction.clickChildViewWithId(
                        R.id.card_measurement_edit)));

        onView(withText(R.string.edit_measurement))
                .inRoot(isDialog()) // <--- checking if the inflated view is a dialog with the textView
                .check(matches(isDisplayed()));

        // tells the ide to wait for the button being clicked before checking
        getInstrumentation().waitForIdleSync();

        // check for the title being displayed - Edit Measurement
        onView(withId(R.id.dialog_measurement_title)).check(matches(isDisplayed()));
        // check for the id field being displayed
        onView(withId(R.id.dialog_measurement_id)).check(matches(isDisplayed()));
        // check for the name field being displayed
        onView(withId(R.id.dialog_measurement_name)).check(matches(isDisplayed()));
        // check for the quantity_kind being displayed
        onView(withId(R.id.dialog_measurement_quantity_kind)).check(matches(isDisplayed()));
        // check for the unit field being displayed
        onView(withId(R.id.dialog_measurement_unit)).check(matches(isDisplayed()));
    }

}
