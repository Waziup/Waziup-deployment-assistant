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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.ClientAuthentication;
import net.openid.appauth.ClientSecretBasic;
import net.openid.appauth.NoClientAuthentication;
import net.openid.appauth.TokenRequest;
import net.openid.appauth.TokenResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.waziup.app.R;
import eu.waziup.app.data.network.model.devices.Device;
import eu.waziup.app.data.network.model.logout.AuthorizationServiceDiscovery;
import eu.waziup.app.data.network.model.sensor.Measurement;
import eu.waziup.app.di.component.ActivityComponent;
import eu.waziup.app.ui.base.BaseFragment;
import eu.waziup.app.ui.neterror.ErrorNetworkFragment;
import eu.waziup.app.ui.sensordetail.SensorDetailDialog;
import eu.waziup.app.utils.CommonUtils;
import timber.log.Timber;

import static eu.waziup.app.ui.main.MainActivity.getDiscoveryDocFromIntent;
import static eu.waziup.app.utils.AppConstants.KEY_AUTH_STATE;
import static eu.waziup.app.utils.AppConstants.KEY_USER_INFO;
import static eu.waziup.app.utils.CommonUtils.getClientSecretFromIntent;

public class DevicesFragment extends BaseFragment implements DevicesMvpView, DevicesAdapter.Callback, DevicesAdapter.MeasurementCallback {

    public static final String TAG = "DevicesFragment";
    @Inject
    DevicesMvpPresenter<DevicesMvpView> mPresenter;

    @Inject
    DevicesAdapter mAdapter;

    @Inject
    AuthorizationService mAuthService;

    @Inject
    LinearLayoutManager mLayoutManager;

    @BindView(R.id.sensor_recycler)
    RecyclerView mRecyclerView;

    @BindView(R.id.sensor_swipe_to_refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.tv_no_sensor)
    TextView tvNoSensors;

    DevicesCommunicator communicator;
    private JSONObject mUserInfoJson;


    private AuthState mAuthState;

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

        handleAuthorization(savedInstanceState);

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
            Log.e("====>mAuthService ", String.valueOf(mAuthService));
            mAuthState.performActionWithFreshTokens(mAuthService, (accessToken, idToken, ex) -> {
                Log.e("====>accessToken ", String.valueOf(accessToken));
                Log.e("====>idToken ", String.valueOf(idToken));
                if (ex != null) {
                    Timber.e("Token refresh failed when fetching user info");
                    return;
                }

                AuthorizationServiceDiscovery discoveryDoc = getDiscoveryDocFromIntent(getBaseActivity().getIntent());
                if (discoveryDoc == null) {
                    // is it necessary to throw Exception inside the app intentionally?
                    throw new IllegalStateException("no available discovery doc");
                }

                mPresenter.loadSensors();

            });
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

    public void handleAuthorization(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(KEY_AUTH_STATE)) {
                try {
                    mAuthState = AuthState.jsonDeserialize(
                            savedInstanceState.getString(KEY_AUTH_STATE));
                } catch (JSONException ex) {
                    Timber.e(ex, "Malformed authorization JSON saved");
                }
            }

            if (savedInstanceState.containsKey(KEY_USER_INFO)) {
                try {
                    mUserInfoJson = new JSONObject(savedInstanceState.getString(KEY_USER_INFO));
                } catch (JSONException ex) {
                    Timber.e(ex, "Failed to parse saved user info JSON");
                }
            }
        }

        if (mAuthState == null) {
            AuthorizationResponse response = AuthorizationResponse.fromIntent(getBaseActivity().getIntent());
            AuthorizationException ex = AuthorizationException.fromIntent(getBaseActivity().getIntent());
            mAuthState = new AuthState(response, ex);

            if (response != null) {
                Timber.d("Received AuthorizationResponse.");
//                getBaseActivity().showSnackbar(R.string.exchange_notification);
                String clientSecret = getClientSecretFromIntent(getBaseActivity().getIntent());
                if (clientSecret != null) {
                    exchangeAuthorizationCode(response, new ClientSecretBasic(clientSecret));
                } else {
                    exchangeAuthorizationCode(response);
                }
            } else {
                Timber.i("Authorization failed: " + ex);
//                showSnackBar(R.string.authorization_failed);
                CommonUtils.toast(getString(R.string.authorization_failed));
            }
        }
    }

    private void exchangeAuthorizationCode(AuthorizationResponse authorizationResponse,
                                           ClientAuthentication clientAuth) {
        performTokenRequest(authorizationResponse.createTokenExchangeRequest(), clientAuth);
    }

    private void exchangeAuthorizationCode(AuthorizationResponse authorizationResponse) {
        performTokenRequest(authorizationResponse.createTokenExchangeRequest());
    }

    private void performTokenRequest(TokenRequest request, ClientAuthentication clientAuth) {
        mAuthService.performTokenRequest(
                request,
                clientAuth,
                this::receivedTokenResponse);//(tokenResponse, ex) -> receivedTokenResponse(tokenResponse, ex)
    }

    private void receivedTokenResponse(
            @Nullable TokenResponse tokenResponse,
            @Nullable AuthorizationException authException) {
        Timber.d("Token request complete");
        mAuthState.update(tokenResponse, authException);
//        showSnackBar((tokenResponse != null)
//                ? getString(R.string.exchange_complete)
//                : getString(R.string.refresh_failed));
        // replacement for the --> showSnackBar()
        CommonUtils.toast((tokenResponse != null)
                ? getString(R.string.exchange_complete)
                : getBaseActivity().getString(R.string.refresh_failed));

        // for updating the access token
        if (tokenResponse != null) {//todo get back here later
//            mPresenter.updateAccessToken(tokenResponse.accessToken);
            //setting the user loggedIn mode for
//            mPresenter.setLoggedInMode();
        }

        // for updating the UI
//        refreshUi();

    }

    private void performTokenRequest(TokenRequest request) {
        performTokenRequest(request, NoClientAuthentication.INSTANCE);
    }

    @Override
    protected void setUp(View view) {
        setUpRecyclerView();
        mAuthState.performActionWithFreshTokens(mAuthService, (accessToken, idToken, ex) -> {
            Log.e("====>accessToken ", String.valueOf(accessToken));
            Log.e("====>idToken ", String.valueOf(idToken));
            if (ex != null) {
                Timber.e("Token refresh failed when fetching user info");
                return;
            }


            mPresenter.loadSensors();

            AuthorizationServiceDiscovery discoveryDoc = getDiscoveryDocFromIntent(getBaseActivity().getIntent());
            if (discoveryDoc == null) {
                // is it necessary to throw Exception inside the app intentionally?
                throw new IllegalStateException("no available discovery doc");
            }


        });

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

    // method for filtering the devices list with the name of the owner
    private List<Device> filterByOwner(List<Device> devices, String predicate) {
        List<Device> filteredList = new ArrayList<>();

        for (Device device : devices) {
            if (device.getOwner().equals(predicate))
                filteredList.add(device);
        }

        return filteredList;
    }

    @Override
    public void showSensors(List<Device> devices) {
        if (devices != null) {
//            Toast.makeText(getBaseActivity(), , Toast.LENGTH_SHORT).show();

            // filtering the devices with the owner name
//            List<Device> filteredDeviceList = filterByOwner(devices, "mikiyasbelhu");//
//            Log.e(TAG, String.format("--->Contains: %d", filteredDeviceList.size()));
//            CommonUtils.toast(String.format("--->Contains: %d", filteredDeviceList.size()));
            if (devices.size() > 0) {
                Timber.e("===devices.size() > 0");
                if (tvNoSensors != null && tvNoSensors.getVisibility() == View.VISIBLE)
                    tvNoSensors.setVisibility(View.GONE);
                if (mRecyclerView != null && mRecyclerView.getVisibility() == View.GONE)
                    mRecyclerView.setVisibility(View.VISIBLE);
                mAdapter.addItems(devices);
            } else {
                Log.e(TAG, "===else");
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
