package eu.waziup.app.ui.devicesdetail;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.thunder413.datetimeutils.DateTimeStyle;
import com.github.thunder413.datetimeutils.DateTimeUtils;
import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import eu.waziup.app.R;
import eu.waziup.app.data.network.model.sensor.Device;
import eu.waziup.app.data.network.model.sensor.Sensor;
import eu.waziup.app.di.component.ActivityComponent;
import eu.waziup.app.ui.base.BaseFragment;
import eu.waziup.app.ui.map.MapFragment;
import eu.waziup.app.ui.sensoredit.EditSensorDialog;
import eu.waziup.app.ui.device.DevicesCommunicator;

import static eu.waziup.app.utils.AppConstants.DETAIL_SENSOR_KEY;

public class DetailDevicesFragment extends BaseFragment implements DetailSensorMvpView, MeasurementAdapter.Callback, MeasurementAdapter.EditCallback, MeasurementAdapter.DeleteCallback {

    @Inject
    DetailDevicesMvpPresenter<DetailSensorMvpView> mPresenter;

    @Inject
    MeasurementAdapter mAdapter;

    @Inject
    LinearLayoutManager mLayoutManager;

    @BindView(R.id.measurement_recycler)
    RecyclerView mRecyclerView;

    @BindView(R.id.tv_no_measurements)
    TextView tvNoMeasurement;

    @BindView(R.id.detail_sensor_domain)
    TextView sensorDomain;

    @BindView(R.id.detail_sensor_domain_title)
    TextView sensorDomainTitle;

    @BindView(R.id.detail_sensor_owner)
    TextView sensorOwner;

    @BindView(R.id.detail_sensor_owner_title)
    TextView sensorOwnerTitle;

    @BindView(R.id.detail_sensor_date)
    TextView sensorDate;

    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;

    @BindView(R.id.btn_locate_on_map)
    ImageView btnSensorLocation;

    public static final String TAG = "DetailDevicesFragment";

    Device mDevice;

    public static String parentFragment;

    public static DetailDevicesFragment newInstance(eu.waziup.app.data.network.model.devices.Device device, String fragmentPassed) {
        Bundle args = new Bundle();
        args.putSerializable(DETAIL_SENSOR_KEY, device);
        DetailDevicesFragment fragment = new DetailDevicesFragment();
        fragment.setArguments(args);
        parentFragment = fragmentPassed;
        return fragment;
    }

    private DevicesCommunicator communicator;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_detail_sensor, container, false);

        ActivityComponent component = getActivityComponent();
        if (component != null) {
            component.inject(this);
            setUnBinder(ButterKnife.bind(this, view));
            mPresenter.onAttach(this);
            mAdapter.setCallback(this);
            mAdapter.setEditCallback(this);
            mAdapter.setDeleteCallback(this);
        }

        if (getArguments() != null)
            mDevice = (Device) getArguments().getSerializable(DETAIL_SENSOR_KEY);

        setUp(view);

        // handle button click event for displaying the devices location on google map
        btnSensorLocation.setOnClickListener(view1 -> {
            if (mDevice != null && mDevice.getLocation() != null)
                openMapFragment(new LatLng(mDevice.getLocation().getLatitude(), mDevice.getLocation().getLongitude()));
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        communicator = (DevicesCommunicator) context;
    }

    @Override
    protected void setUp(View view) {
        communicator.invisibleFab();
        loadPage(mDevice);
        setUpRecyclerView();
    }

    @OnClick(R.id.detail_sensor_add_measurement)
    void onAddMeasurementClicked() {
        mPresenter.onAddSensorsClicked();
    }

//    @OnClick(R.id.btn_deploy)
//    void onDeployClicked() {
//        mPresenter.onDeployDevicesClicked();
//    }
//
//    @OnClick(R.id.btn_undeploy)
//    void onUndeployClicked() {
//        mPresenter.onUnDeployDevicesClicked();
//    }

    @OnClick(R.id.btn_locate_on_map)
    void onLocateOnMapClicked() {
        if (mDevice != null && mDevice.getLocation() != null)
            openMapFragment(new LatLng(mDevice.getLocation().getLatitude(), mDevice.getLocation().getLongitude()));
    }

    private void setUpRecyclerView() {
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);
    }

    @OnClick(R.id.nav_back_btn)
    void onNavBackClick() {
        getBaseActivity().onFragmentDetached(TAG, parentFragment);
    }

    @Override
    public void onDestroyView() {
        mPresenter.onDetach();
        super.onDestroyView();
    }

    @Override
    public void showSensors(String devicesId, List<Sensor> sensors) {
        if (sensors != null) {
            if (sensors.size() > 0) {
                if (tvNoMeasurement != null && tvNoMeasurement.getVisibility() == View.VISIBLE)
                    tvNoMeasurement.setVisibility(View.GONE);
                if (mRecyclerView != null && mRecyclerView.getVisibility() == View.GONE)
                    mRecyclerView.setVisibility(View.VISIBLE);
                mAdapter.addItems(devicesId, sensors);
            } else {
                if (mRecyclerView != null && mRecyclerView.getVisibility() == View.VISIBLE)
                    mRecyclerView.setVisibility(View.GONE);
                if (mRecyclerView != null && mRecyclerView.getVisibility() == View.GONE) {
                    tvNoMeasurement.setVisibility(View.VISIBLE);
                    tvNoMeasurement.setText(R.string.no_measurements_list_found);
                }
            }
        }
        hideLoading();
    }

    @Override
    public void loadPage(Device device) {
        if (mDevice != null) {

            toolbarTitle.setText((TextUtils.isEmpty(mDevice.getId())) ? mDevice.getName() : mDevice.getId());

            showLoading();
            // todo tvNoMeasurements for the sensors and the whole device is different should be implemented
            tvNoMeasurement.setVisibility(View.GONE);
            showSensors(mDevice.getId(), mDevice.getSensors());

            if (mDevice.getLocation() != null) {
                btnSensorLocation.setVisibility(View.VISIBLE);
            }

            if (!TextUtils.isEmpty(mDevice.getDateCreated()))
                sensorDate.setText(String.valueOf(DateTimeUtils.formatWithStyle(mDevice.getDateCreated(),
                        DateTimeStyle.MEDIUM)));

            if (!TextUtils.isEmpty(mDevice.getOwner())) {
                sensorOwnerTitle.setVisibility(View.VISIBLE);
                sensorOwner.setVisibility(View.VISIBLE);
                sensorOwner.setText(String.valueOf(mDevice.getOwner()));
            } else {
                sensorOwnerTitle.setVisibility(View.GONE);
                sensorOwner.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(mDevice.getDomain())) {
                sensorDomainTitle.setVisibility(View.VISIBLE);
                sensorDomain.setVisibility(View.VISIBLE);
                sensorDomain.setText(String.valueOf(mDevice.getDomain()));
            } else {
                sensorDomainTitle.setVisibility(View.GONE);
                sensorDomain.setVisibility(View.GONE);
            }

//        } else {// todo there has to be a way of expression the tvNoMeasurement in here in the else clause

        }
    }

    @Override
    public void showCreateSensorsDialog() {
//        EditSensorDialog dialog = new EditSensorDialog(getBaseActivity(), new Sensor(), mPresenter);
//        if (dialog.getWindow() != null) {
//            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        }
//        dialog.show();
    }

    @Override
    public void openMapFragment(LatLng latLng) {
        if (getBaseActivity() != null){
            getBaseActivity().getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                    .replace(R.id.cl_root_view, MapFragment.newInstance(latLng), MapFragment.TAG)
                    .commit();
        }
    }

    @Override
    public void onItemClicked(Sensor sensor) {
        // add some content to be displayed here when clicking on the sensor
    }

    @Override
    public void onItemEditClicked(Sensor sensor) {
        EditSensorDialog.newInstance(sensor).show(getBaseActivity().getSupportFragmentManager(), "");
    }

    @Override
    public void onItemDeleteClicked(String sensorId, Sensor sensor) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getBaseActivity());
        builder.setMessage("Are you sure you want to delete sensor?")
                .setPositiveButton("Delete", (dialog, id) -> {
                    mPresenter.onDeleteSensorClicked(sensorId, sensor.getId());
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel", (dialog, id) -> {
                    dialog.dismiss();
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onBackPressed() {

    }
}