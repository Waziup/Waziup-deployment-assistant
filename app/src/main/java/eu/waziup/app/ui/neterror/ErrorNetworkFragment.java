package eu.waziup.app.ui.neterror;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.OnClick;
import eu.waziup.app.R;
import eu.waziup.app.di.component.ActivityComponent;
import eu.waziup.app.ui.base.BaseFragment;
import eu.waziup.app.ui.devicesdetail.DetailDevicesFragment;
import eu.waziup.app.ui.notification.NotificationFragment;
import eu.waziup.app.ui.notificationdetail.NotificationDetailFragment;
import eu.waziup.app.ui.register.RegisterSensorFragment;
import eu.waziup.app.ui.device.DevicesCommunicator;
import eu.waziup.app.ui.device.DevicesFragment;

public class ErrorNetworkFragment extends BaseFragment implements ErrorNetworkMvpView {

    @Inject
    ErrorNetworkMvpPresenter<ErrorNetworkMvpView> mPresenter;

    public static final String TAG = "ErrorNetworkFragment";
    public static String CURRENT_TAG = "";
    public static String parent;
    private Handler mHandler;

    DevicesCommunicator communicator;

    public static ErrorNetworkFragment newInstance(String pts) {
        Bundle args = new Bundle();
        parent = pts;
        ErrorNetworkFragment fragment = new ErrorNetworkFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_error_net, container, false);

        ActivityComponent component = getActivityComponent();
        if (component != null) {
            component.inject(this);
            setUnBinder(ButterKnife.bind(this, view));
            mPresenter.onAttach(this);
        }

        mHandler = new Handler();

        setUp(view);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        communicator = (DevicesCommunicator) context;
    }

    @OnClick(R.id.btn_retry)
    void onRefreshClicked() {
        mPresenter.onRefreshClicked(parent);
    }

    @Override
    protected void setUp(View view) {
        communicator.invisibleFab();
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public void showSensorFragment(String parent) {
        Fragment fragment = null;
        Class fragmentClass = null;

        switch (parent) {
            case DevicesFragment.TAG:
                fragmentClass = DevicesFragment.class;
                break;
            case NotificationFragment.TAG:
                fragmentClass = NotificationFragment.class;
                break;
            case RegisterSensorFragment.TAG:
                fragmentClass = RegisterSensorFragment.class;
                break;
            case DetailDevicesFragment.TAG:
                fragmentClass = DevicesFragment.class;
                break;
            case NotificationDetailFragment.TAG:
                fragmentClass = NotificationDetailFragment.class;
                break;
        }

        try {
            if (fragmentClass != null)
                fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Fragment finalFragment = fragment;
        Runnable mPendingRunnable = () -> {
            // Insert the fragment by replacing any existing fragment
            FragmentManager fragmentManager = getBaseActivity().getSupportFragmentManager();
            if (finalFragment != null)
                fragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                        .replace(R.id.flContent, finalFragment, CURRENT_TAG)
                        .commit();

        };

        // If mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }
    }
}
