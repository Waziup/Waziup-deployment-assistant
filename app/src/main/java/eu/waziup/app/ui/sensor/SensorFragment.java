package eu.waziup.app.ui.sensor;

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
import eu.waziup.app.R;
import eu.waziup.app.data.network.model.sensor.Sensor;
import eu.waziup.app.di.component.ActivityComponent;
import eu.waziup.app.ui.base.BaseFragment;

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
        if (getBaseActivity().getSupportActionBar() != null)
            getBaseActivity().getSupportActionBar().setTitle(R.string.sensors);
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
                if (tvNoSensors != null && tvNoSensors.getVisibility() == View.VISIBLE)
                    tvNoSensors.setVisibility(View.GONE);
                if (mRecyclerView != null && mRecyclerView.getVisibility() == View.GONE)
                    mRecyclerView.setVisibility(View.VISIBLE);
                mAdapter.addItems(sensors);
            } else {
                if (tvNoSensors != null && tvNoSensors.getVisibility() == View.GONE) {
                    tvNoSensors.setVisibility(View.VISIBLE);
                    tvNoSensors.setText(R.string.no_sensors_list_found);
                }
                if (mRecyclerView != null && mRecyclerView.getVisibility() == View.VISIBLE)
                    mRecyclerView.setVisibility(View.GONE);
            }
        }
        hideLoading();
    }

    @Override
    public void openDetailSensorActivity(Sensor sensor) {

    }

    @Override
    public void loadPage() {
//        mPresenter.loadSensors();
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
