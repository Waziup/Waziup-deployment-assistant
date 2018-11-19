package eu.waziup.waziup_da_app.ui.sensor;

import android.content.Context;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import eu.waziup.waziup_da_app.R;
import eu.waziup.waziup_da_app.data.network.model.sensor.Sensor;
import eu.waziup.waziup_da_app.di.component.ActivityComponent;
import eu.waziup.waziup_da_app.ui.base.BaseFragment;
import eu.waziup.waziup_da_app.ui.detail.DetailSensorFragment;
import eu.waziup.waziup_da_app.ui.main.MainActivity;
import eu.waziup.waziup_da_app.ui.register.RegisterSensorFragment;
import eu.waziup.waziup_da_app.utils.CommonUtils;

public class SensorFragment extends BaseFragment implements SensorMvpView, SensorAdapter.Callback {

    @Inject
    SensorMvpPresenter<SensorMvpView> mPresenter;

    @Inject
    SensorAdapter mAdapter;

    @Inject
    LinearLayoutManager mLayoutManager;

    @BindView(R.id.sensor_recycler)
    RecyclerView mRecyclerView;

    @BindView(R.id.sensor_swipe_to_refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.tv_no_sensor)
    TextView tvNoSensors;

    SensorCommunicator communicator;

    public static final String TAG = "SensorFragment";

    public static SensorFragment newInstance() {
        Bundle args = new Bundle();
        SensorFragment fragment = new SensorFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sensor, container, false);

        ActivityComponent component = getActivityComponent();
        if (component != null) {
            component.inject(this);
            setUnBinder(ButterKnife.bind(this, view));
            mPresenter.onAttach(this);
            mAdapter.setCallback(this);
        }

        setUp(view);

        mPresenter.loadSensors();

        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            mPresenter.loadSensors();
            mSwipeRefreshLayout.setRefreshing(false);
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        communicator = (SensorCommunicator) context;
    }

    @OnClick(R.id.fab_sensor)
    void onFabClicked() {
        communicator.fabClicked();
    }

    @Override
    protected void setUp(View view) {
        setUpRecyclerView();
        mPresenter.loadSensors();
    }

    private void setUpRecyclerView() {
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void showSensors(List<Sensor> sensors) {
        if (sensors != null) {
            if (sensors.size() > 0) {
                tvNoSensors.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
                mAdapter.addItems(sensors);
            } else {
                mRecyclerView.setVisibility(View.GONE);
                tvNoSensors.setVisibility(View.VISIBLE);
                tvNoSensors.setText(R.string.no_sensors_list_found);
            }
        }
        hideLoading();
    }

    @Override
    public void openDetailSensorActivity(Sensor sensor) {

    }

    @Override
    public void loadPage() {
        mPresenter.loadSensors();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Drawable drawable = item.getIcon();
        if (drawable instanceof Animatable) {
            ((Animatable) drawable).start();
        }
        return true;
    }

    @Override
    public void onItemClicked(Sensor sensor) {
        communicator.onItemClicked(sensor);
    }

}
