package eu.waziup.waziup_da_app.ui.map;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import eu.waziup.waziup_da_app.R;
import eu.waziup.waziup_da_app.data.network.model.sensor.Sensor;
import eu.waziup.waziup_da_app.di.component.ActivityComponent;
import eu.waziup.waziup_da_app.ui.base.BaseFragment;

public class MapFragment extends BaseFragment implements MapMvpView {

    @Inject
    MapMvpPresenter<MapMvpView> mPresenter;

    private MapView mapView;

    List<Sensor> sensorList = new ArrayList<>();


    public static final String TAG = "MapFragment";

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

        mPresenter.loadSensors();

        mapView.getMapAsync(mapboxMap -> {
            // One way to add a marker view

            //Filling up the list
            for (int i = 0; i < sensorList.size(); i++) {
                if (sensorList.get(i).getLocation() != null) {
                    if (sensorList.get(i).getLocation().getLatitude() != null &&
                            sensorList.get(i).getLocation().getLongitude() != null)
                        mapboxMap.addMarker(new MarkerOptions()
                                .position(new LatLng(sensorList.get(i).getLocation().getLatitude(), sensorList.get(i).getLocation().getLongitude()))
                                .title(sensorList.get(i).getId()));
                }
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mPresenter.onAttach(this);
    }

    @Override
    protected void setUp(View view) {

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
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mapView.onDestroy();
    }

    @Override
    public void showSensorsOnMap(List<Sensor> sensors) {
        hideLoading();
        sensorList.addAll(sensors);
    }

//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        mapView.onSaveInstanceState(outState);
//    }
}
