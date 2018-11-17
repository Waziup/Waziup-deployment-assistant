package eu.waziup.waziup_da_app.ui.register;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import eu.waziup.waziup_da_app.R;
import eu.waziup.waziup_da_app.di.component.ActivityComponent;
import eu.waziup.waziup_da_app.ui.base.BaseFragment;

import static android.app.Activity.RESULT_OK;

public class RegisterSensorFragment extends BaseFragment implements RegisterSensorMvpView {

    @Inject
    RegisterSensorMvpPresenter<RegisterSensorMvpView> mPresenter;

    @BindView(R.id.dialog_sensor_visibility)
    Spinner mSpinner;

    //todo current location by default should be stored in sharedPreference and later be displayed
    @BindView(R.id.register_current_location_value)
    EditText currentLocation;

    @BindView(R.id.btn_register_get_current_location)
    ImageView btnCurrentLocation;

    public final static int SCANNING_REQUEST_CODE = 1;


    public static final String TAG = "RegisterSensorFragment";

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

        });

        String action = getActivity().getIntent().getAction();
        if (action != null && action.equals(ACTION_SCAN_CODE)) {
            checkPermissionOrToScan();
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
//        switch (requestCode) {
//            case SCANNING_REQUEST_CODE:
//                if (resultCode == RESULT_OK) {
//                    final Bundle bundle = data.getExtras();
//                    Handler handler = new Handler(Looper.getMainLooper());
//                    handler.post(() -> {
//                        if (bundle != null)
//                            textView.setText(bundle.getString("result"));
//                        // nothing
//                    });
//                }
//                break;
//            default:
//                break;
//        }

        switch (requestCode) {
            case SCANNING_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    if (null != bundle) {
                        editTextNumber.setText(bundle.getString("result"));
                    }
                }
                break;
            default:
                break;
        }
    }

    private void hideImm() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(fab.getWindowToken(), 0);
        }
    }

    private void checkPermissionOrToScan() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Notice: Do not use the below code.
            // ActivityCompat.requestPermissions(getActivity(),
            // new String[] {Manifest.permission.CAMERA}, 1);
            // Such code may still active the request permission dialog
            // but even the user has granted the permission,
            // app will response nothing.
            // The below code works perfect.
            requestPermissions(new String[] { Manifest.permission.CAMERA }, REQUEST_CAMERA_PERMISSION_CODE);
        } else {
            startScanningActivity();
        }
    }

    private void startScanningActivity() {
        try {
            Intent intent = new Intent(getContext(), CaptureActivity.class);
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
