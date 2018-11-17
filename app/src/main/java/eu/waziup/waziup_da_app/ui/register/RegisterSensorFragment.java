package eu.waziup.waziup_da_app.ui.register;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.google.zxing.Result;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import eu.waziup.waziup_da_app.R;
import eu.waziup.waziup_da_app.di.component.ActivityComponent;
import eu.waziup.waziup_da_app.ui.base.BaseFragment;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.Manifest.permission.CAMERA;

public class RegisterSensorFragment extends BaseFragment implements RegisterSensorMvpView, ZXingScannerView.ResultHandler {

    @Inject
    RegisterSensorMvpPresenter<RegisterSensorMvpView> mPresenter;

    @BindView(R.id.dialog_sensor_visibility)
    Spinner mSpinner;

    //todo current location by default should be stored in sharedPreference and later be displayed
    @BindView(R.id.register_current_location_value)
    EditText currentLocation;

    @BindView(R.id.btn_register_get_current_location)
    ImageView btnCurrentLocation;

    private ZXingScannerView mScannerView;

    private static final int REQUEST_CAMERA = 100;

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

        return view;
    }

    @OnClick(R.id.nav_back_btn)
    void onNavBackClick() {
        getBaseActivity().onFragmentDetached(TAG);
    }

    @OnClick(R.id.register_scan_qr)
    void onScanClicked() {
//        CommonUtils.toast("QR Scanner");
        ScanQrCodeDialog dialog = new ScanQrCodeDialog(getBaseActivity());
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        dialog.show();
    }

    @Override
    protected void setUp(View view) {
        initScannerView();
    }

    private void initScannerView() {
        mScannerView = new ZXingScannerView(getBaseActivity());
        mScannerView.setAutoFocus(true);
        mScannerView.setResultHandler(this);
//        view.addvie.addView(mScannerView);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(getBaseActivity(), new String[]{CAMERA}, REQUEST_CAMERA);
    }

    private boolean checkPermission() {
        return (ContextCompat.checkSelfPermission(getBaseActivity(), CAMERA) == PackageManager.PERMISSION_GRANTED);
    }

    private void doRequestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkPermission()) {
                // todo has to add the qr scanning code here
            } else {
                requestPermission();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    initScannerView();

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                    return;
                }

            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    @Override
    public void onStart() {
        doRequestPermission();
        super.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void onDestroyView() {
        mPresenter.onDetach();
        super.onDestroyView();
    }

    @Override
    public void handleResult(Result result) {
        // Do something with the result here
//        Log.v(TAG, rawResult.getText()); // Prints scan results
//        Log.v(TAG, rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode, pdf417 etc.)

        // If you would like to resume scanning, call this method below:
//        mScannerView.resumeCameraPreview(this);
    }
}
