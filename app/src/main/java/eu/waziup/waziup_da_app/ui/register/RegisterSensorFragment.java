package eu.waziup.waziup_da_app.ui.register;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import eu.waziup.waziup_da_app.R;
import eu.waziup.waziup_da_app.di.component.ActivityComponent;
import eu.waziup.waziup_da_app.ui.base.BaseFragment;
import eu.waziup.waziup_da_app.utils.CommonUtils;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.app.Activity.RESULT_OK;

public class RegisterSensorFragment extends BaseFragment implements RegisterSensorMvpView {

    @Inject
    RegisterSensorMvpPresenter<RegisterSensorMvpView> mPresenter;

    @BindView(R.id.register_sensor_visibility)
    Spinner mSpinner;

    @BindView(R.id.register_sensor_id)
    EditText sensorId;

    @BindView(R.id.register_sensor_name)
    EditText sensorName;

    @BindView(R.id.register_sensor_domain)
    EditText sensorDomain;

    //todo current location by default should be stored in sharedPreference and later be displayed
    @BindView(R.id.register_current_location_value)
    EditText currentLocation;

    @BindView(R.id.btn_register_get_current_location)
    ImageView btnCurrentLocation;

    public static final String ACTION_SCAN_CODE = "eu.waziup.waziup_da_app.ui.main.MainActivity";

    public final static int SCANNING_REQUEST_CODE = 1;

    public final static int REQUEST_CAMERA_PERMISSION_CODE = 0;

    public final static int REQUEST_LOCATION_PERMISSION_CODE = 2;

    public static final String TAG = "RegisterSensorFragment";

    double longitude, latitude;

    public final LocationListener locationListener = null;

    ZXingScannerView mScannerView;

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

        btnCurrentLocation.setOnClickListener(view1 -> {
            //todo should get the current location of the user and displays dialog with famous 5 places
            CommonUtils.toast("btnCurrentLocation clicked");
        });

        if (getActivity() != null) {
            String action = getActivity().getIntent().getAction();
            if (action != null && action.equals(ACTION_SCAN_CODE)) {
                checkPermissionOrToScan();
            }
        }

        setHasOptionsMenu(true);

        return view;
    }

    @OnClick(R.id.nav_back_btn)
    void onNavBackClick() {
        getBaseActivity().onFragmentDetached(TAG);
    }

    @OnClick(R.id.register_scan_qr)
    void onScanClicked() {

        checkPermissionOrToScan();
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
                            Log.e("---->", String.valueOf(bundle.getString("result")));
                        } catch (JSONException e) {
                            e.printStackTrace();

                            Log.e("---->JSONException", String.valueOf(bundle.getString("result")));
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

    }

    @Override
    public void onDestroyView() {
        mPresenter.onDetach();
        super.onDestroyView();
    }

}
