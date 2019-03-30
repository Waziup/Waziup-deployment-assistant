package eu.waziup.app.ui.sensordetail;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import eu.waziup.app.data.network.model.sensor.Measurement;
import eu.waziup.app.data.network.model.sensor.Sensor;
import eu.waziup.app.di.component.ActivityComponent;
import eu.waziup.app.ui.base.BaseFragment;
import eu.waziup.app.ui.map.MapFragment;
import eu.waziup.app.ui.measurementedit.EditMeasurementDialog;
import eu.waziup.app.ui.sensor.SensorCommunicator;

import static eu.waziup.app.utils.AppConstants.DETAIL_SENSOR_KEY;

public class DetailSensorFragment extends BaseFragment implements DetailSensorMvpView, MeasurementAdapter.Callback, MeasurementAdapter.EditCallback, MeasurementAdapter.DeleteCallback {

    @Inject
    DetailSensorMvpPresenter<DetailSensorMvpView> mPresenter;

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

    public static final String TAG = "DetailSensorFragment";

    Sensor mSensor;

    public static String parentFragment;

    //    https://stackoverflow.com/questions/9931993/passing-an-object-from-an-activity-to-a-fragment
    public static DetailSensorFragment newInstance(Sensor sensor, String fragmentPassed) {
        Bundle args = new Bundle();
        args.putSerializable(DETAIL_SENSOR_KEY, sensor);
        DetailSensorFragment fragment = new DetailSensorFragment();
        fragment.setArguments(args);
        parentFragment = fragmentPassed;
        return fragment;
    }

//    NOT BEING USED
//    public static NotificationDetailFragment newInstance(Sensor sensor) {
//        Bundle args = new Bundle();
//        args.putSerializable(DETAIL_SENSOR_KEY, sensor);
//        NotificationDetailFragment fragment = new NotificationDetailFragment();
//        fragment.setArguments(args);
//        return fragment;
//    }

    private SensorCommunicator communicator;

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
            mSensor = (Sensor) getArguments().getSerializable(DETAIL_SENSOR_KEY);

        setUp(view);

        // map button on clickListener
        btnSensorLocation.setOnClickListener(view1 -> {
            if (mSensor != null && mSensor.getLocation() != null)
                openMapFragment(new LatLng(mSensor.getLocation().getLatitude(), mSensor.getLocation().getLongitude()));
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        communicator = (SensorCommunicator) context;
    }

    @Override
    protected void setUp(View view) {
        communicator.invisibleFab();
        loadPage(mSensor);
        setUpRecyclerView();
    }

    @OnClick(R.id.detail_sensor_add_measurement)
    void onAddMeasurementClicked() {
        mPresenter.onAddMeasurementClicked();
    }

//    @OnClick(R.id.btn_deploy)
//    void onDeployClicked() {
//        mPresenter.onDeploySensorClicked();
//    }
//
//    @OnClick(R.id.btn_undeploy)
//    void onUndeployClicked() {
//        mPresenter.onUnDeploySensorClicked();
//    }

    @OnClick(R.id.btn_locate_on_map)
    void onLocateOnMapClicked() {
        if (mSensor != null && mSensor.getLocation() != null)
            openMapFragment(new LatLng(mSensor.getLocation().getLatitude(), mSensor.getLocation().getLongitude()));
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
    public void showMeasurements(String sensorId, List<Measurement> measurements) {
        if (measurements != null) {
            if (measurements.size() > 0) {
                if (tvNoMeasurement != null && tvNoMeasurement.getVisibility() == View.VISIBLE)
                    tvNoMeasurement.setVisibility(View.GONE);
                if (mRecyclerView != null && mRecyclerView.getVisibility() == View.GONE)
                    mRecyclerView.setVisibility(View.VISIBLE);
                mAdapter.addItems(sensorId, measurements);
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
    public void loadPage(Sensor sensor) {
        if (mSensor != null) {

            toolbarTitle.setText((TextUtils.isEmpty(mSensor.getId())) ? mSensor.getName() : mSensor.getId());

            showLoading();
            // todo tvNoMeasurements for the measurements and the whole sensor is different should be implemented
            tvNoMeasurement.setVisibility(View.GONE);
            showMeasurements(mSensor.getId(), mSensor.getMeasurements());

            if (mSensor.getLocation() != null) {
                btnSensorLocation.setVisibility(View.VISIBLE);
            }

            if (!TextUtils.isEmpty(mSensor.getDateCreated()))
                sensorDate.setText(String.valueOf(DateTimeUtils.formatWithStyle(mSensor.getDateCreated(),
                        DateTimeStyle.MEDIUM)));

            if (!TextUtils.isEmpty(mSensor.getOwner())) {
                sensorOwnerTitle.setVisibility(View.VISIBLE);
                sensorOwner.setVisibility(View.VISIBLE);
                sensorOwner.setText(String.valueOf(mSensor.getOwner()));
            } else {
                sensorOwnerTitle.setVisibility(View.GONE);
                sensorOwner.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(mSensor.getDomain())) {
                sensorDomainTitle.setVisibility(View.VISIBLE);
                sensorDomain.setVisibility(View.VISIBLE);
                sensorDomain.setText(String.valueOf(mSensor.getDomain()));
            } else {
                sensorDomainTitle.setVisibility(View.GONE);
                sensorDomain.setVisibility(View.GONE);
            }

//        } else {// todo there has to be a way of expression the tvNoMeasurement in here in the else clause

        }
    }

    @Override
    public void showCreateMeasurementsDialog() {
//        EditMeasurementDialog dialog = new EditMeasurementDialog(getBaseActivity(), new Measurement(), mPresenter);
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
    public void onItemClicked(Measurement measurement) {
//        CommonUtils.toast("onItemDeleteClicked");
    }

    @Override
    public void onItemEditClicked(Measurement measurement) {
        EditMeasurementDialog.newInstance().show(getBaseActivity().getSupportFragmentManager(), "");
//        EditMeasurementDialog dialog = new EditMeasurementDialog(getBaseActivity(), measurement, mPresenter);
//        if (dialog.getWindow() != null) {
//            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        }
//        dialog.show();
    }

    @Override
    public void onItemDeleteClicked(String sensorId, Measurement measurement) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getBaseActivity());
        builder.setMessage("Are you sure you want to delete measurement?")
                .setPositiveButton("Delete", (dialog, id) -> {
                    mPresenter.onDeleteMeasurementClicked(sensorId, measurement.getId());
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