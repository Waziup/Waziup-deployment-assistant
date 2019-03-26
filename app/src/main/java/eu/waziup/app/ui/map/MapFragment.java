package eu.waziup.app.ui.map;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.OnClick;
import eu.waziup.app.R;
import eu.waziup.app.data.network.model.sensor.Sensor;
import eu.waziup.app.di.component.ActivityComponent;
import eu.waziup.app.ui.base.BaseFragment;
import eu.waziup.app.ui.sensor.SensorCommunicator;
import eu.waziup.app.utils.CommonUtils;

public class MapFragment extends BaseFragment implements MapMvpView, MapboxMap.OnInfoWindowClickListener, PermissionsListener {

    @Inject
    MapMvpPresenter<MapMvpView> mPresenter;

    private MapView mapView;

    List<Sensor> sensorList = new ArrayList<>();

    MapCommunicator communicator;
    SensorCommunicator SensorCommunicator;
    //    private static final int PERMISSIONS_LOCATION = 9910;
    public static final String TAG = "MapFragment";

    // variables for adding location layer
    private MapboxMap map;
    private PermissionsManager permissionsManager;
    private Location originLocation;

    public static LatLng sensorLatLng;

    public static MapFragment newInstance(LatLng latLng) {
        Bundle args = new Bundle();
        MapFragment fragment = new MapFragment();
        fragment.setArguments(args);
        sensorLatLng = latLng;
        return fragment;
    }

    public static MapFragment newInstance() {
        Bundle args = new Bundle();
        MapFragment fragment = new MapFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_map, container, false);

        ActivityComponent component = getActivityComponent();
        if (component != null) {
            component.inject(this);
            setUnBinder(ButterKnife.bind(this, view));
            mPresenter.onAttach(this);
        }

        mapView = view.findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);

        setUp(view);

        mapView.getMapAsync(mapboxMap -> {

            map = mapboxMap;

            enableLocationComponent(mapboxMap);

            // checks if permission has been granted already and if so it starts Location indicator
            // checkPermissionAndEnableLocation();
            mapboxMap.setOnInfoWindowClickListener(this);

            // custom info window for the markers
            mapboxMap.setInfoWindowAdapter(marker -> {

                View v = getLayoutInflater().inflate(R.layout.layout_callout, null);

                TextView sensorName = v.findViewById(R.id.info_sensor_name);
                TextView sensorDomain = v.findViewById(R.id.info_sensor_domain);

                sensorName.setText(marker.getTitle());
                sensorDomain.setText(marker.getSnippet());

                return v;

            });

            // setting the UI for the mapView
            settingUi();

        });

        mPresenter.loadSensors();

        return view;
    }

    @Override
    protected void setUp(View view) {
        SensorCommunicator.invisibleFab();

    }

    @OnClick(R.id.gps_fab)
    void onFabClicked() {

        if (map != null)
            if (originLocation != null && originLocation.getLatitude() != 0 && originLocation.getLongitude() != 0)
                updateMap(originLocation.getLatitude(), originLocation.getLongitude(), map);

    }

    public void settingUi() {
        if (map != null) {
            map.getUiSettings().setZoomControlsEnabled(false);//hide zoom control button
            map.getUiSettings().setCompassEnabled(false);//hide compass
            map.getUiSettings().setRotateGesturesEnabled(false);
            map.getUiSettings().setLogoEnabled(false);
            map.getUiSettings().setZoomGesturesEnabled(true);
            map.getUiSettings().setScrollGesturesEnabled(false);
            map.getUiSettings().setRotateGesturesEnabled(false);
            map.getUiSettings().setAllGesturesEnabled(true);
        }
    }

    /**
     * --> VERY USEFUL
     * For animating the camera to the specific location on a map
     *
     * @param latitude  (required)
     * @param longitude (required)
     */
    private void updateMap(double latitude, double longitude, MapboxMap mapboxMap) {

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(latitude, longitude)) // Sets the new camera position
                .zoom(19) // Sets the zoom
                .bearing(180) // Rotate the camera
                .tilt(30) // Set the camera tilt
                .build(); // Creates a CameraPosition from the builder

        mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 7000, null);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        communicator = (MapCommunicator) context;
        SensorCommunicator = (SensorCommunicator) context;
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    public void onStop() {
        mapView.onStop();
        super.onStop();
    }

    @Override
    public void onLowMemory() {
        mapView.onLowMemory();
        super.onLowMemory();
    }

    @Override
    public void onDestroyView() {
        mapView.onDestroy();
        super.onDestroyView();
    }

    @Override
    public void showSensorsOnMap(List<Sensor> sensors) {
        hideLoading();
        sensorList.addAll(sensors);

        // One way to add a marker view
        // Filling up the list
        if (map != null)
            for (int i = 0; i < sensorList.size(); i++) {
                if (sensorList.get(i).getLocation() != null) {
                    if (sensorList.get(i).getLocation().getLatitude() != null &&
                            sensorList.get(i).getLocation().getLongitude() != null)
                        map.addMarker(new MarkerOptions()
                                .position(new LatLng(sensorList.get(i).getLocation().getLatitude(),
                                        sensorList.get(i).getLocation().getLongitude()))
                                .title(String.valueOf(sensorList.get(i).getId())))
                                .setSnippet(String.valueOf(sensorList.get(i).getDomain()));
                }
            }
    }

    @SuppressWarnings({"MissingPermission"})
    private void enableLocationComponent(MapboxMap mapboxMap) {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(getBaseActivity())) {
            // Activate the MapboxMap LocationComponent to show user location
            // Adding in LocationComponentOptions is also an optional parameter
            LocationComponent locationComponent = mapboxMap.getLocationComponent();
            locationComponent.activateLocationComponent(getBaseActivity());
            locationComponent.setLocationComponentEnabled(true);
            // Set the component's camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING);
            originLocation = locationComponent.getLastKnownLocation();

        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(getBaseActivity());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        CommonUtils.toast("This app needs location permissions in order to show its functionality.");
    }

    // todo find a better implementation for this activity
    private void toggleGps(boolean enableGps) {
        if (enableGps) {
            // Check if user has granted location permission
            permissionsManager = new PermissionsManager(this);
            if (!PermissionsManager.areLocationPermissionsGranted(getBaseActivity())) {
                permissionsManager.requestLocationPermissions(getBaseActivity());
            } else {
//                enableLocation(true);
            }
        } else {
//            enableLocation(false);
        }
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            if (map != null)
                enableLocationComponent(map);
        } else {
            CommonUtils.toast("You didn't grant location permissions.");
//            finish();
        }
    }

//    private boolean checkPermissions() {
//        if (ContextCompat.checkSelfPermission(this,
//                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            return true;
//        } else {
//            requestPermissions();
//            return false;
//        }
//    }

//    private void requestPermissions() {
//        ActivityCompat.requestPermissions(this,
//                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                REQUEST_FINE_LOCATION);
//    }

    // Trigger new location updates at interval
//    protected void startLocationUpdates() {
//
//        // Create the location request to start receiving updates
//        mLocationRequest = new LocationRequest();
//        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        mLocationRequest.setInterval(UPDATE_INTERVAL);
//        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
//
//        // Create LocationSettingsRequest object using location request
//        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
//        builder.addLocationRequest(mLocationRequest);
//        LocationSettingsRequest locationSettingsRequest = builder.build();
//
//        // Check whether location settings are satisfied
//        // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
//        SettingsClient settingsClient = LocationServices.getSettingsClient(getBaseActivity());
//        settingsClient.checkLocationSettings(locationSettingsRequest);
//
//
//        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
//        if (ActivityCompat.checkSelfPermission(getBaseActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getBaseActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        LocationServices.getFusedLocationProviderClient(getBaseActivity()).requestLocationUpdates(mLocationRequest,
//                new LocationCallback() {
//                    @Override
//                    public void onLocationResult(LocationResult locationResult) {
//                        // do work here
//                        onLocationChanged(locationResult.getLastLocation());
//                    }
//                },
//                Looper.myLooper());
//    }

//    public void getLastLocation() {
//        // Get last known recent location using new Google Play Services SDK (v11+)
//        FusedLocationProviderClient locationClient = LocationServices.getFusedLocationProviderClient(getBaseActivity());
//
//        if (ActivityCompat.checkSelfPermission(getBaseActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getBaseActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        locationClient.getLastLocation()
//                .addOnSuccessListener(location -> {
//                    // GPS location can be null if GPS is switched off
//                    if (location != null) {
//                        onLocationChanged(location);
//                    }
//                })
//                .addOnFailureListener(e -> {
//                    Log.d("MapDemoActivity", "Error trying to get last GPS location");
//                    e.printStackTrace();
//                });
//    }

//    public void onLocationChanged(Location location) {
//        // New location has now been determined
//        String msg = "Updated Location: " +
//                Double.toString(location.getLatitude()) + "," +
//                Double.toString(location.getLongitude());
////        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
//        // You can now create a LatLng Object for use with maps
//        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
//    }

//    private void checkPermissionAndEnableLocation() {
//        if (!PermissionsManager.areLocationPermissionsGranted(getBaseActivity())) {
//            ActivityCompat.requestPermissions(getBaseActivity(), new String[]{
//                    Manifest.permission.ACCESS_COARSE_LOCATION,
//                    Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_LOCATION);
//        } else {
//            enableLocation();
//        }
//    }

//    private void enableLocation() {
//        map.setMyLocationEnabled(true);
//    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        if (requestCode == PERMISSIONS_LOCATION) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                enableLocation();
//                CommonUtils.toast("Location permission granted");
//            }
//        }
//    }

    @Override
    public boolean onInfoWindowClick(@NonNull Marker marker) {
        if (sensorList.size() > 0) {
            Sensor selectedSensor = null;
            for (Sensor sensor : sensorList) {
                if (TextUtils.equals(sensor.getId(), marker.getTitle())) {
                    selectedSensor = sensor;
                }
            }
            if (selectedSensor != null)
                communicator.onMarkerClicked(selectedSensor, TAG);
        }
        return true;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

}
