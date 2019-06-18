package eu.waziup.app.ui.device;

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

import com.annimon.stream.Stream;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.waziup.app.R;
import eu.waziup.app.data.network.model.devices.Device;
import eu.waziup.app.data.network.model.sensor.Measurement;
import eu.waziup.app.di.component.ActivityComponent;
import eu.waziup.app.ui.base.BaseFragment;
import eu.waziup.app.ui.sensordetail.SensorDetailDialog;
import eu.waziup.app.ui.neterror.ErrorNetworkFragment;

public class DevicesFragment extends BaseFragment implements DevicesMvpView, DevicesAdapter.Callback, DevicesAdapter.MeasurementCallback {

    @Inject
    DevicesMvpPresenter<DevicesMvpView> mPresenter;

    @Inject
    DevicesAdapter mAdapter;

    @Inject
    LinearLayoutManager mLayoutManager;

    @BindView(R.id.sensor_recycler)
    RecyclerView mRecyclerView;

    @BindView(R.id.sensor_swipe_to_refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.tv_no_sensor)
    TextView tvNoSensors;

    DevicesCommunicator communicator;

    public static final String TAG = "DevicesFragment";

    public static DevicesFragment newInstance() {
        Bundle args = new Bundle();
        DevicesFragment fragment = new DevicesFragment();
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
            mAdapter.setMeasurementCallback(this);
        }

        setUp(view);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 || dy < 0 && communicator.isFabShown())
                    communicator.hideFab();
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE)
                    communicator.showFab();
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            mPresenter.loadSensors();
            mSwipeRefreshLayout.setRefreshing(false);
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        loadPage();
        communicator = (DevicesCommunicator) context;
    }

    @Override
    protected void setUp(View view) {
        setUpRecyclerView();
        mPresenter.loadSensors();
        if (getBaseActivity().getSupportActionBar() != null)
            getBaseActivity().getSupportActionBar().setTitle(R.string.devices);

        // Telling the MainActivity to make the Fab visible
        communicator.visibleFab();
    }

    private void setUpRecyclerView() {
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);
    }

    private List<Device> filter(List<Device> devices, String predicate){

        new Stream<List<Device>>(() -> devices.stream().filter(device -> device.getOwner().equals(predicate)).collect(Collectors.toList()))

        return devices.stream().filter(device -> device.getOwner().equals(predicate)).collect(Collectors.toList());
    }

    @Override
    public void showSensors(List<Device> devices) {
        if (devices != null) {
            if (devices.size() > 0) {
                if (tvNoSensors != null && tvNoSensors.getVisibility() == View.VISIBLE)
                    tvNoSensors.setVisibility(View.GONE);
                if (mRecyclerView != null && mRecyclerView.getVisibility() == View.GONE)
                    mRecyclerView.setVisibility(View.VISIBLE);
                mAdapter.addItems(devices);
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
    public void loadPage() {
//        mPresenter.loadSensors();
    }

    @Override
    public void showNetworkErrorPage() {
        getBaseActivity().getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                .replace(R.id.flContent, ErrorNetworkFragment.newInstance(DevicesFragment.TAG), ErrorNetworkFragment.TAG)
                .commit();
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
    public void onItemClicked(Device device) {
        communicator.onItemClicked(device);
    }

    @Override
    public void onItemClicked(Measurement measurement) {
        SensorDetailDialog.newInstance(measurement).show(getBaseActivity().getSupportFragmentManager(), "");
    }

    @Override
    public void onBackPressed() {

    }
}
