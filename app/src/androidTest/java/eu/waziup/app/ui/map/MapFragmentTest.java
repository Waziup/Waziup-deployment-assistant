package eu.waziup.app.ui.map;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;

import com.mapbox.mapboxsdk.geometry.LatLng;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import eu.waziup.app.R;
import eu.waziup.app.ui.main.MainActivity;

public class MapFragmentTest {

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
                .replace(R.id.flContent, MapFragment.newInstance(new LatLng( 9.0476, 38.7600)),
                        MapFragment.TAG)
                .commit();
    }

    @Test
    public void mapAndMarkersDisplayed() throws Exception {

    }

    @Test
    public void performMarkerClicked() throws Exception {

    }
}