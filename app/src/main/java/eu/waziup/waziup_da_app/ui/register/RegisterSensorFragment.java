package eu.waziup.waziup_da_app.ui.register;

import android.os.Bundle;
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
import me.dm7.barcodescanner.zxing.ZXingScannerView;

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
