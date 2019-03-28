package eu.waziup.app.ui.register;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import eu.waziup.app.R;
import eu.waziup.app.data.network.model.sensor.Sensor;
import eu.waziup.app.di.component.ActivityComponent;
import eu.waziup.app.ui.base.BaseFragment;
import eu.waziup.app.ui.sensor.SensorCommunicator;
import eu.waziup.app.ui.sensor.SensorFragment;
import eu.waziup.app.utils.CommonUtils;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.LOCATION_SERVICE;
import static java.lang.String.format;

public class RegisterSensorFragment extends BaseFragment implements RegisterSensorMvpView, LocationListener {

    @Inject
    RegisterSensorMvpPresenter<RegisterSensorMvpView> mPresenter;

    @BindView(R.id.register_sensor_visibility)
    Spinner sensorVisibility;

    @BindView(R.id.register_sensor_id)
    EditText sensorId;

    @BindView(R.id.register_sensor_name)
    EditText sensorName;

    @BindView(R.id.register_sensor_domain)
    EditText sensorDomain;

//    @BindView(R.id.register_sensor_gateway)
//    EditText sensorGateway;

    @BindView(R.id.register_current_location_value)
    EditText sensorLocation;

    // flag for GPS status
    boolean isGPSEnabled = false;
    // flag for network status
    boolean isNetworkEnabled = false;
    // flag for GPS status
    boolean canGetLocation = false;
    Location location; // location
    double latitude; // latitude
    double longitude; // longitude
    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute
    // Declaring a Location Manager
    protected LocationManager locationManager;

    public final static int SCANNING_REQUEST_CODE = 1;

    public final static int REQUEST_CAMERA_PERMISSION_CODE = 0;

    public final static int REQUEST_LOCATION_PERMISSION_CODE = 2;

    public static final String TAG = "RegisterSensorFragment";

    SensorCommunicator communicator;

    public static RegisterSensorFragment newInstance() {
        Bundle args = new Bundle();
        RegisterSensorFragment fragment = new RegisterSensorFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_register_sensor, container, false);

        ActivityComponent component = getActivityComponent();
        if (component != null) {
            component.inject(this);
            setUnBinder(ButterKnife.bind(this, view));
            mPresenter.onAttach(this);
        }

        setUp(view);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        communicator = (SensorCommunicator) context;
    }

    @OnClick(R.id.btn_register_get_current_location)
    void onGetLocationClicked() {
        if (getView() != null)
            getLocation(getView());
        if (canGetLocation()) {
            latitude = getLatitude();
            longitude = getLongitude();
            //                // \n is for new line
//                Toast.makeText(getBaseActivity(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
            sensorLocation.setText(format("Lat %.4f", latitude) + ",  " + format("Long %.4f", longitude));
        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            showSettingsAlert();
        }
    }

    @OnClick(R.id.nav_back_btn)
    void onNavBackClick() {
        getBaseActivity().onFragmentDetached(TAG, SensorFragment.TAG);
    }

    @OnClick(R.id.register_scan_qr)
    void onScanClicked() {
        checkPermissionOrToScan();
    }

    @OnClick(R.id.btn_register_submit)
    void onSubmitClicked() {

        // ID
        if (TextUtils.isEmpty(sensorId.getText())) {
            sensorId.requestFocus();
            sensorId.setError("sensorId can't be empty");
            return;
        }

        // Name
        if (TextUtils.isEmpty(sensorName.getText())) {
            sensorName.requestFocus();
            sensorName.setError("sensorName can't be empty");
            return;
        }

        // Domain
        if (TextUtils.isEmpty(sensorDomain.getText())) {
            sensorDomain.requestFocus();
            sensorDomain.setError("sensorDomain can't be empty");
            return;
        }

//        // Gateway
//        if (TextUtils.isEmpty(sensorGateway.getText())) {
//            sensorGateway.requestFocus();
//            sensorGateway.setError("gateway can't be empty");
//            return;
//        }

        // Location
        if (TextUtils.isEmpty(sensorLocation.getText())) {
            sensorLocation.requestFocus();
            sensorLocation.setError("location can't be empty");
            return;
        }

        // Visibility
        String mSensorVisibility = "";
        if (sensorVisibility.getSelectedItem() == null) {
            mSensorVisibility = "public";
        }

        mPresenter.onSubmitRegisterClicked(
                new Sensor(sensorId.getText().toString().trim(),
                        sensorName.getText().toString().trim(),
                        sensorDomain.getText().toString().trim(),
                        TextUtils.isEmpty(mSensorVisibility) ? sensorVisibility.getSelectedItem().toString().trim() : mSensorVisibility,
                        new eu.waziup.app.data.network.model.sensor.Location(latitude, longitude)
//                        ,sensorGateway.getText().toString().trim()
                )
        );
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SCANNING_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    if (null != bundle) {
                        try {
                            //converting the data to json
                            JSONObject obj = new JSONObject(bundle.getString("result"));
                            //setting values to textviews
                            sensorId.setText(obj.getString("sensor_id"));
                            sensorName.setText(obj.getString("sensor_name"));
                            sensorDomain.setText(obj.getString("domain"));
//                            Log.e("---->", String.valueOf(bundle.getString("result")));
                        } catch (JSONException e) {
                            e.printStackTrace();

//                            Log.e("---->JSONException", String.valueOf(bundle.getString("result")));
                            Toast.makeText(getBaseActivity(), String.valueOf(bundle.getString("result")),
                                    Toast.LENGTH_LONG).show();
                        }
//                        Toast.makeText(getBaseActivity(), , Toast.LENGTH_SHORT).show();
//                        editTextNumber.setText(bundle.getString("result"));
                    }
                }
                break;
            case REQUEST_LOCATION_PERMISSION_CODE:
                if (resultCode == RESULT_OK) {
                    if (data != null)
                        if (data.getExtras() != null)
                            CommonUtils.toast(data.getExtras().toString());
                }
                break;
            default:
                break;
        }
    }

    private void checkPermissionOrToScan() {
        if (getContext() != null)
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION_CODE);
            } else {
                startScanningActivity();
            }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA_PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startScanningActivity();
                } else {
                    getBaseActivity().hideKeyboard(getBaseActivity());
                    if (getContext() != null) {
                        AlertDialog dialog = new AlertDialog.Builder(getContext())
                                .setTitle(R.string.permission_denied)
                                .setMessage(R.string.require_permission)
                                .setPositiveButton(R.string.go_to_settings, (dialog1, which) -> {

                                    // Go to the detail settings of our application
                                    Intent intent = new Intent();
                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", getContext().getPackageName(), null);
                                    intent.setData(uri);
                                    startActivity(intent);

                                    dialog1.dismiss();
                                })
                                .setNegativeButton(android.R.string.cancel, (dialog12, which) -> dialog12.dismiss())
                                .create();
                        dialog.show();
                    }
                }
                break;
            case REQUEST_LOCATION_PERMISSION_CODE:
                // Request for camera permission.
                if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission has been granted. Start camera preview Activity.
                    if (getView() != null)
                        Snackbar.make(getView(), R.string.location_permission_granted,
                                Snackbar.LENGTH_SHORT)
                                .show();
                    latitude = getLatitude();
                    longitude = getLongitude();
                } else {
                    // Permission request was denied.
                    // todo display alert dialog rather than snackBar
                    if (getView() != null)
                        Snackbar.make(getView(), R.string.location_permission_denied,
                                Snackbar.LENGTH_SHORT)
                                .show();

//                    AlertDialog dialog = new AlertDialog.Builder(getContext())
//                            .setTitle(R.string.permission_denied)
//                            .setMessage(R.string.require_permission)
//                            .setPositiveButton(R.string.go_to_settings, (dialog1, which) -> {
//
//                                //todo handle this later
//                                // Go to the detail settings of our application
////                                    Intent intent = new Intent();
////                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
////                                    Uri uri = Uri.fromParts("package", getContext().getPackageName(), null);
////                                    intent.setData(uri);
////                                    startActivity(intent);
//
//                                dialog1.dismiss();
//                            })
//                            .setNegativeButton(android.R.string.cancel, (dialog12, which) -> dialog12.dismiss())
//                            .create();
//                    dialog.show();
                }
                break;
            default:

        }
    }

    private void startScanningActivity() {
        try {
            Intent intent = new Intent(getContext(), SimpleScannerActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivityForResult(intent, SCANNING_REQUEST_CODE);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void setUp(View view) {
        // hiding the Fab from the MainActivity
        communicator.invisibleFab();
    }

    @Override
    public void onDestroyView() {
        mPresenter.onDetach();
        super.onDestroyView();
    }

    public Location getLocation(View view) {
        try {
            locationManager = (LocationManager) getBaseActivity().getSystemService(LOCATION_SERVICE);
            // getting GPS status
            if (locationManager != null) {
                isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

                // getting network status
                isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                if (!isGPSEnabled && !isNetworkEnabled) {
                    // no network provider is enabled
                } else {
                    this.canGetLocation = true;


                    if (ContextCompat.checkSelfPermission(getBaseActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED &&
                            ContextCompat.checkSelfPermission(getBaseActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                                    == PackageManager.PERMISSION_GRANTED) {

                        if (isNetworkEnabled) {
                            locationManager.requestLocationUpdates(
                                    LocationManager.NETWORK_PROVIDER,
                                    MIN_TIME_BW_UPDATES,
                                    MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
//                            Log.d("Network", "Network");
                            if (locationManager != null) {
                                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                                if (location != null) {
                                    latitude = location.getLatitude();
                                    longitude = location.getLongitude();
                                }
                            }
                        }
                        // if GPS Enabled get lat/long using GPS Services
                        if (isGPSEnabled) {
                            if (location == null) {
                                locationManager.requestLocationUpdates(
                                        LocationManager.GPS_PROVIDER,
                                        MIN_TIME_BW_UPDATES,
                                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
//                                Log.d("GPS Enabled", "GPS Enabled");
                                if (locationManager != null) {
                                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                    if (location != null) {
                                        latitude = location.getLatitude();
                                        longitude = location.getLongitude();
                                    }
                                }
                            }
                        }

                    } else {
                        requestLocationPermission(view);
                    }

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return location;
    }

    private void requestLocationPermission(View view) {
        // Permission has not been granted and must be requested.
        if (ActivityCompat.shouldShowRequestPermissionRationale(getBaseActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)) {

            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // Display a SnackBar with cda button to request the missing permission.
            Snackbar.make(view, R.string.location_access_required,
                    Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, view1 -> {
                // Request the permission
                ActivityCompat.requestPermissions(getBaseActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION},
                        REQUEST_LOCATION_PERMISSION_CODE);
            }).show();

        } else {
            Snackbar.make(view, R.string.location_permission_not_available, Snackbar.LENGTH_SHORT).show();
            // Request the permission. The result will be received in onRequestPermissionResult().
            ActivityCompat.requestPermissions(getBaseActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION_PERMISSION_CODE);
        }
    }

    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app
     */
    public void stopUsingGPS() {
        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }
    }

    /**
     * Function to get latitude
     */
    public double getLatitude() {
        if (location != null) {
            latitude = location.getLatitude();
        }
        // return latitude
        return latitude;
    }

    /**
     * Function to get longitude
     */
    public double getLongitude() {
        if (location != null) {
            longitude = location.getLongitude();
        }
        // return longitude
        return longitude;
    }

    /**
     * Function to check GPS/wifi enabled
     *
     * @return boolean
     */
    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    /**
     * Function to show settings alert dialog
     * On pressing Settings button will lauch Settings Options
     */
    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getBaseActivity());
        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");
        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", (dialog, which) -> {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            getBaseActivity().startActivity(intent);
        });
        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        // Showing Alert Message
        alertDialog.show();
    }

    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void openSensorListFragment() {
        hideLoading();
        getBaseActivity().onFragmentDetached(TAG, SensorFragment.TAG);
    }

    @Override
    public void onBackPressed() {

    }
}
