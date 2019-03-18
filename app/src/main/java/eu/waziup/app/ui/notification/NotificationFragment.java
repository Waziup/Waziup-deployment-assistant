package eu.waziup.app.ui.notification;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.waziup.app.R;
import eu.waziup.app.data.network.model.notification.NotificationResponse;
import eu.waziup.app.di.component.ActivityComponent;
import eu.waziup.app.ui.base.BaseFragment;
import eu.waziup.app.ui.sensor.SensorCommunicator;
import eu.waziup.app.ui.sensor.SensorFragment;

public class NotificationFragment extends BaseFragment implements NotificationMvpView, NotificationAdapter.Callback {

    @Inject
    NotificationMvpPresenter<NotificationMvpView> mPresenter;

    @Inject
    NotificationAdapter mAdapter;

    @Inject
    LinearLayoutManager mLayoutManager;

    @BindView(R.id.notification_recycler)
    RecyclerView mRecyclerView;

    @BindView(R.id.notification_swipe_to_refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;

//    @BindView(R.id.fragment_toolbar)
//    TextView mToolbarTitle;

    @BindView(R.id.tv_no_notification)
    TextView tvNoNotification;

    SensorCommunicator communicator;

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
    public void onAttach(Context context) {
        super.onAttach(context);
        communicator = (SensorCommunicator) context;
    }


    @Override
    protected void setUp(View view) {
        if (getBaseActivity().getSupportActionBar() != null)
            getBaseActivity().getSupportActionBar().setTitle(getString(R.string.notification));
        setUpRecyclerView();

        // hiding the Fab from the MainActivity
        communicator.hideFab();
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
                if (tvNoNotification != null && tvNoNotification.getVisibility() == View.VISIBLE)
                    tvNoNotification.setVisibility(View.GONE);
                if (mRecyclerView != null && mRecyclerView.getVisibility() == View.GONE)
                    mRecyclerView.setVisibility(View.VISIBLE);
                mAdapter.addItems(notificationResponses);
            } else {
                if (tvNoNotification != null && tvNoNotification.getVisibility() == View.GONE) {
                    tvNoNotification.setVisibility(View.VISIBLE);
                    tvNoNotification.setText(R.string.no_notification_list_found);
                }
                if (mRecyclerView != null && mRecyclerView.getVisibility() == View.VISIBLE)
                    mRecyclerView.setVisibility(View.GONE);
            }
        }
        hideLoading();
    }

    @Override
    public void onItemClicked(NotificationResponse notification) {

    }
}
