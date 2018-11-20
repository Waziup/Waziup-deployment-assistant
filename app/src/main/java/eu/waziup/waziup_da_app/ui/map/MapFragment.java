package eu.waziup.waziup_da_app.ui.map;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import eu.waziup.waziup_da_app.R;
import eu.waziup.waziup_da_app.data.network.model.sensor.Sensor;
import eu.waziup.waziup_da_app.di.component.ActivityComponent;
import eu.waziup.waziup_da_app.ui.base.BaseFragment;

public class MapFragment extends BaseFragment implements MapMvpView, MapboxMap.OnInfoWindowClickListener {

    @Inject
    MapMvpPresenter<MapMvpView> mPresenter;

    private MapView mapView;

    List<Sensor> sensorList = new ArrayList<>();

    MapCommunicator communicator;


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

        return view;
    }

    @Override
    protected void setUp(View view) {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        communicator = (MapCommunicator) context;
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

        mapView.getMapAsync(mapboxMap -> {
            // One way to add a marker view
            //Filling up the list
            for (int i = 0; i < sensorList.size(); i++) {
                if (sensorList.get(i).getLocation() != null) {
                    if (sensorList.get(i).getLocation().getLatitude() != null &&
                            sensorList.get(i).getLocation().getLongitude() != null)
                        mapboxMap.addMarker(new MarkerOptions()
                                .position(new LatLng(sensorList.get(i).getLocation().getLatitude(), sensorList.get(i).getLocation().getLongitude()))
                                .title(String.valueOf(sensorList.get(i).getId())))
                                .setSnippet(String.valueOf(sensorList.get(i).getDomain()));
                }
            }

            mapboxMap.setInfoWindowAdapter(marker -> {


                View view = getBaseActivity().getLayoutInflater().inflate(R.layout.layout_callout, null);
                if (marker.getInfoWindow() != null) {
                    TextView sensorName = view.findViewById(R.id.info_sensor_name);
                    TextView sensorDomain = view.findViewById(R.id.info_sensor_domain);

                    sensorName.setText(marker.getTitle());
                    sensorDomain.setText(marker.getSnippet());
                }

                return view;

            });

            mapboxMap.setOnInfoWindowClickListener(this);
        });
    }

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

//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        mapView.onSaveInstanceState(outState);
//    }
}
