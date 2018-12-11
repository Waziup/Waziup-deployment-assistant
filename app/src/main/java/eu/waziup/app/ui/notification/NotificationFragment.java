package eu.waziup.app.ui.notification;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.waziup.app.R;
import eu.waziup.app.data.network.model.notification.NotificationResponse;
import eu.waziup.app.data.network.model.sensor.Sensor;
import eu.waziup.app.di.component.ActivityComponent;
import eu.waziup.app.ui.base.BaseFragment;
import eu.waziup.app.ui.main.MainMvpPresenter;
import eu.waziup.app.ui.main.MainMvpView;
import eu.waziup.app.ui.sensor.SensorAdapter;

public class NotificationFragment extends BaseFragment implements NotificationMvpView, NotificationAdapter.Callback {

    @Inject
    NotificationMvpPresenter<NotificationMvpView> mPresenter;

    @Inject
    NotificationAdapter mAdapter;

    @Inject
    LinearLayoutManager mLayoutManager;

    @BindView(R.id.sensor_recycler)
    RecyclerView mRecyclerView;

    @BindView(R.id.sensor_swipe_to_refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;

    public static final String TAG = "NotificationFragment";

    public static NotificationFragment newInstance() {
        Bundle args = new Bundle();
        NotificationFragment fragment = new NotificationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        ActivityComponent component = getActivityComponent();
        if (component != null) {
            component.inject(this);
            setUnBinder(ButterKnife.bind(this, view));
            mPresenter.onAttach(this);
            mAdapter.setCallback(this);
        }

        setUp(view);

        mPresenter.loadNotifications();

        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            mPresenter.loadNotifications();
            mSwipeRefreshLayout.setRefreshing(false);
        });

        return view;
    }

    @Override
    protected void setUp(View view) {
        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void showNotifications(List<NotificationResponse> notificationResponses) {
        if (notificationResponses != null) {
            if (notificationResponses.size() > 0) {
                if (tvNoSensors != null && tvNoSensors.getVisibility() == View.VISIBLE)
                    tvNoSensors.setVisibility(View.GONE);
                if (mRecyclerView != null && mRecyclerView.getVisibility() == View.GONE)
                    mRecyclerView.setVisibility(View.VISIBLE);
                mAdapter.addItems(notificationResponses);
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
    public void onItemClicked(Sensor sensor) {

    }
}
