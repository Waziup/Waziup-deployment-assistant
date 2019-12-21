package eu.waziup.app.ui.device;

import android.content.Context;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import eu.waziup.app.data.network.model.sensor.Sensor;
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

    /**
     * a method for giving the instance of this this fragment for helping start the fragment from
     * another activity class
     * @return a DevicesFragment instance of this fragment
     */
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

        // handles the animation for hiding the floating action button in the screen to be automatically
        // hidden and appear when scrolling
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

        // handle the api calling for refreshing the page when swiping down for refreshing the page
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            mAuthState.performActionWithFreshTokens(mAuthService, (accessToken, idToken, ex) -> {
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

    /**
     * method being called when the fragment containing this screen starts. when being attached to
     * the activity
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        loadPage();
        communicator = (DevicesCommunicator) context;
    }

    /**
     * method for handling the authorization when opening the screen
     * checks if the user is already authenticated user from the saveInstanceState
     * and starts the authorization process if the user hasn't been authenticated yet
     * @param savedInstanceState Activities have the ability, under special circumstances, to
     *                           restore themselves to a previous state using the data stored in
     *                           this bundle.
     */
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

    /**
     * handle the token response after token exchange happens
     * @param tokenResponse if not null then it contains the token for authorization
     * @param authException if not null then it contains the error message for why the authorization when wrong
     */
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

    /**
     * performs token request for exchangingAuthorization to get a token
     * @param request
     */
    private void performTokenRequest(TokenRequest request) {
        performTokenRequest(request, NoClientAuthentication.INSTANCE);
    }

    /**
     * a method that setups important things on opening the screen. Things like:
     *      - setting up the recyclerView
     *      - checking the authentication: whether the token has expired and needs to refresh or not
     * @param view since its a fragment view the application opens
     */
    @Override
    protected void setUp(View view) {
        setUpRecyclerView();
        mAuthState.performActionWithFreshTokens(mAuthService, (accessToken, idToken, ex) -> {
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

    /**
     * prepares the recyclerView list view on the screen without data
     */
    private void setUpRecyclerView() {
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);
    }

    /**
     * a method for filtering the devices list with a specific user when a user logs in
     * @param devices the list of devices that need to be filtered
     * @param predicate the name of the owner used on filtering
     * @return a list devices which have been filtered with a specific owner name
     */
    private List<Device> filterByOwner(List<Device> devices, String predicate) {
        List<Device> filteredList = new ArrayList<>();

        for (Device device : devices) {
            if (device.getOwner().equals(predicate))
                filteredList.add(device);
        }

        return filteredList;
    }

    /**
     * a method handling for showing the sensors list coming from the presenter
     * checks if the list is empty, if so it shows "No devices list found." message and
     * sets the lists in the adapter for display if there is even one
     * @param devices
     */
    @Override
    public void showDevices(List<Device> devices) {
        if (devices != null) {
//            Toast.makeText(getBaseActivity(), , Toast.LENGTH_SHORT).show();

            // filtering the devices with the owner name
//            List<Device> filteredDeviceList = filterByOwner(devices, "mikiyasbelhu");//
//            Log.e(TAG, String.format("--->Contains: %d", filteredDeviceList.size()));
//            CommonUtils.toast(String.format("--->Contains: %d", filteredDeviceList.size()));
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

    /**
     * opens a screen showing no  network right now and try again kind of message screen when
     * the network is out and when its required to perform the action
     */
    @Override
    public void showNetworkErrorPage() {
        getBaseActivity().getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                .replace(R.id.flContent, ErrorNetworkFragment.newInstance(DevicesFragment.TAG), ErrorNetworkFragment.TAG)
                .commit();
    }

    @RequiresApi(api = Build.VERSION_CODES.DONUT)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Drawable drawable = item.getIcon();
        if (drawable instanceof Animatable) {
            ((Animatable) drawable).start();
        }
        return true;
    }

    /**
     * handle the item click on a device for opening the device detail screen
     * @param device the selected device item object
     */
    @Override
    public void onItemClicked(Device device) {
        communicator.onItemClicked(device);
    }

    /**
     * handles the item click listener for when the measurements on the horizontal view item
     * @param sensor the selected sensor item object
     */
    @Override
    public void onItemClicked(Sensor sensor) {
        SensorDetailDialog.newInstance(sensor).show(getBaseActivity().getSupportFragmentManager(), "");
    }

    @Override
    public void onBackPressed() {

    }
}
