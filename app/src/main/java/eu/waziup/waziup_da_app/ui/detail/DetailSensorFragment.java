package eu.waziup.waziup_da_app.ui.detail;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.thunder413.datetimeutils.DateTimeStyle;
import com.github.thunder413.datetimeutils.DateTimeUtils;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import eu.waziup.waziup_da_app.R;
import eu.waziup.waziup_da_app.data.network.model.sensor.Measurement;
import eu.waziup.waziup_da_app.data.network.model.sensor.Sensor;
import eu.waziup.waziup_da_app.di.component.ActivityComponent;
import eu.waziup.waziup_da_app.ui.base.BaseFragment;
import eu.waziup.waziup_da_app.utils.CommonUtils;

import static eu.waziup.waziup_da_app.utils.AppConstants.DETAIL_SENSOR_KEY;

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

    public static final String TAG = "DetailSensorFragment";

    Sensor mSensor;

    //    https://stackoverflow.com/questions/9931993/passing-an-object-from-an-activity-to-a-fragment
    public static DetailSensorFragment newInstance(Sensor sensor) {
        Bundle args = new Bundle();
        args.putSerializable(DETAIL_SENSOR_KEY, sensor);
        DetailSensorFragment fragment = new DetailSensorFragment();
        fragment.setArguments(args);
        return fragment;
    }

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

        return view;
    }

    @Override
    protected void setUp(View view) {

        setUpRecyclerView();

        if (mSensor != null) {

            toolbarTitle.setText((TextUtils.isEmpty(mSensor.getId())) ? mSensor.getName() : mSensor.getId());

            showLoading();
            // todo tvNoMeasurements for the measurements and the whole sensor is different should be implemented
            tvNoMeasurement.setVisibility(View.GONE);
            showMeasurements(mSensor.getMeasurements());

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

        } else {// todo there has to be a way of expression the tvNoMeasurement in here in the else clause

        }

    }

    @OnClick(R.id.btn_deploy)
    void onDeployClicked() {
        mPresenter.onDeploySensorClicked();
    }

    @OnClick(R.id.btn_undeploy)
    void onUndeployClicked() {
        mPresenter.onUndeploySensorClicked();
    }

    @OnClick(R.id.sensor_detail_locate_on_map)
    void onLocateOnMapClicked() {
        CommonUtils.toast("boom locate map opened");
    }

    private void setUpRecyclerView() {
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);
    }

    @OnClick(R.id.nav_back_btn)
    void onNavBackClick() {
        getBaseActivity().onFragmentDetached(TAG);
    }

    @Override
    public void onDestroyView() {
        mPresenter.onDetach();
        super.onDestroyView();
    }

    @Override
    public void showMeasurements(List<Measurement> measurements) {
        if (measurements != null) {
            if (measurements.size() > 0) {
                tvNoMeasurement.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
                mAdapter.addItems(measurements);
            } else {
                mRecyclerView.setVisibility(View.GONE);
                tvNoMeasurement.setVisibility(View.VISIBLE);
                tvNoMeasurement.setText(R.string.no_measurements_list_found);
            }
        }
        hideLoading();
    }

    @Override
    public void onItemClicked(Measurement measurement) {
        CommonUtils.toast("onItemClicked");
    }

    @Override
    public void onItemEditClicked(Measurement measurement) {
        CommonUtils.toast("onItemEditClicked");
    }

    @Override
    public void onItemDeleteClicked(Measurement measurement) {
        CommonUtils.toast("onItemDeleteClicked");
    }
}