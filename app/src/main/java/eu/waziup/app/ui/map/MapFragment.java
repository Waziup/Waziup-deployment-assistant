package eu.waziup.app.ui.map;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import eu.waziup.app.ui.sensor.SensorFragment;

public class MapFragment extends BaseFragment implements MapMvpView {

    //    private static final int PERMISSIONS_LOCATION = 9910;
    public static final String TAG = "MapFragment";
    @Inject
    MapMvpPresenter<MapMvpView> mPresenter;
    List<Sensor> sensorList = new ArrayList<>();
    MapCommunicator communicator;
    SensorCommunicator SensorCommunicator;
    // variables for adding location layer
    private Location originLocation;

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

        setUp(view);


        mPresenter.loadSensors();

        return view;
    }

    @OnClick(R.id.nav_back_btn)
    void onNavBackClick() {
        getBaseActivity().onFragmentDetached(TAG, SensorFragment.TAG);
    }

    @Override
    protected void setUp(View view) {
        SensorCommunicator.invisibleFab();
    }

    @OnClick(R.id.gps_fab)
    void onFabClicked() {

    }

    public void settingUi() {
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        communicator = (MapCommunicator) context;
        SensorCommunicator = (SensorCommunicator) context;
    }


    @Override
    public void showSensorsOnMap(List<Sensor> sensors) {
        hideLoading();
        sensorList.addAll(sensors);


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    }

    // todo find a better implementation for this activity
    private void toggleGps(boolean enableGps) {

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {

    }
}
