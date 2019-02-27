package eu.waziup.app.ui.register;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import eu.waziup.app.R;
import eu.waziup.app.ui.main.MainActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.*;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;

public class SimpleScannerActivityTest {

    @Rule
    public ActivityTestRule<SimpleScannerActivity> rule =
            new ActivityTestRule<>(SimpleScannerActivity.class, true, false);


    @Before
    public void setUp() throws Exception {
        // launching the activity
        rule.launchActivity(new Intent());

    }

    @Test
    public void onEditFieldsAndButtonsDisplayed() {
        // toolbar title
        onView(withId(R.id.simple_scanner_toolbar)).check(matches(hasDescendant(withText(R.string.scan_sensor))));
    }
}