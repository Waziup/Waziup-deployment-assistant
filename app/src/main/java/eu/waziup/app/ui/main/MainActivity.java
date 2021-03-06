package eu.waziup.app.ui.main;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.jakewharton.threetenabp.AndroidThreeTen;
import com.mapbox.mapboxsdk.Mapbox;
import com.squareup.picasso.Picasso;

import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.ClientAuthentication;
import net.openid.appauth.ClientSecretBasic;
import net.openid.appauth.NoClientAuthentication;
import net.openid.appauth.TokenRequest;
import net.openid.appauth.TokenResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.waziup.app.BuildConfig;
import eu.waziup.app.R;
import eu.waziup.app.data.network.model.devices.Device;
import eu.waziup.app.data.network.model.logout.AuthorizationServiceDiscovery;
import eu.waziup.app.data.network.model.logout.LogoutRequest;
import eu.waziup.app.data.network.model.logout.LogoutService;
import eu.waziup.app.ui.base.BaseActivity;
import eu.waziup.app.ui.custom.RoundedImageView;
import eu.waziup.app.ui.device.DevicesCommunicator;
import eu.waziup.app.ui.device.DevicesFragment;
import eu.waziup.app.ui.devicesdetail.DetailDevicesFragment;
import eu.waziup.app.ui.login.LoginActivity;
import eu.waziup.app.ui.map.MapCommunicator;
import eu.waziup.app.ui.map.MapFragment;
import eu.waziup.app.ui.notification.NotificationFragment;
import eu.waziup.app.ui.register.RegisterSensorFragment;
import eu.waziup.app.utils.CommonUtils;
import timber.log.Timber;

import static eu.waziup.app.utils.AppConstants.EXTRA_AUTH_SERVICE_DISCOVERY;
import static eu.waziup.app.utils.AppConstants.EXTRA_CLIENT_SECRET;
import static eu.waziup.app.utils.AppConstants.KEY_AUTH_STATE;
import static eu.waziup.app.utils.AppConstants.KEY_USER_INFO;
import static eu.waziup.app.utils.CommonUtils.getClientSecretFromIntent;

public class MainActivity extends BaseActivity implements MainMvpView, DevicesCommunicator, MapCommunicator {

    public static final String TAG = MainActivity.class.getSimpleName();

    // AUTHORIZATION VARIABLES
    private static final int BUFFER_SIZE = 1024;
    public static String CURRENT_TAG = DevicesFragment.TAG;

    @Inject
    MainMvpPresenter<MainMvpView> mPresenter;

    @Inject
    AuthorizationService mAuthService;

    @BindView(R.id.main_toolbar)
    Toolbar mToolbar;

    @BindView(R.id.drawer_view)
    DrawerLayout mDrawer;

    @BindView(R.id.flContent)
    FrameLayout frameLayout;

    @BindView(R.id.navigation_view)
    NavigationView nvDrawer;

    @BindView(R.id.fab_sensor)
    FloatingActionButton fabSensor;

    private RoundedImageView mProfileView;
    private TextView mNameTextView;
    private TextView mEmailTextView;
    private ActionBarDrawerToggle mDrawerToggle;

    // keycloak auth variables
    private AuthState mAuthState;
    private JSONObject mUserInfoJson;

    private Handler mHandler;

    public static Intent getStartIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }

    public static AuthorizationServiceDiscovery getDiscoveryDocFromIntent(Intent intent) {
        if (!intent.hasExtra(EXTRA_AUTH_SERVICE_DISCOVERY)) {
            return null;
        }
        String discoveryJson = intent.getStringExtra(EXTRA_AUTH_SERVICE_DISCOVERY);
        try {
            return new AuthorizationServiceDiscovery(new JSONObject(discoveryJson));
        } catch (JSONException | AuthorizationServiceDiscovery.MissingArgumentException ex) {
            throw new IllegalStateException("Malformed JSON in discovery doc");
            // todo find a way for handling this malformed JSON
        }
    }

    public static PendingIntent createPostAuthorizationIntent(
            @NonNull Context context,
            @NonNull AuthorizationRequest request,
            @Nullable net.openid.appauth.AuthorizationServiceDiscovery discoveryDoc,
            @Nullable String clientSecret) {
        Intent intent = new Intent(context, MainActivity.class);
        if (discoveryDoc != null) {
            intent.putExtra(EXTRA_AUTH_SERVICE_DISCOVERY, discoveryDoc.docJson.toString());
        }
        if (clientSecret != null) {
            intent.putExtra(EXTRA_CLIENT_SECRET, clientSecret);
        }

        return PendingIntent.getActivity(context, request.hashCode(), intent, 0);
    }


    private static String readStream(InputStream stream) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(stream));
        char[] buffer = new char[BUFFER_SIZE];
        StringBuilder sb = new StringBuilder();
        int readCount;
        while ((readCount = br.read(buffer)) != -1) {
            sb.append(buffer, 0, readCount);
        }
        return sb.toString();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, BuildConfig.MAPBOX_TOKEN);
        AndroidThreeTen.init(this);

        setContentView(R.layout.activity_main);

        getActivityComponent().inject(this);
        setUnBinder(ButterKnife.bind(this));
        mPresenter.onAttach(MainActivity.this);

        mHandler = new Handler();

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
            AuthorizationResponse response = AuthorizationResponse.fromIntent(getIntent());
            AuthorizationException ex = AuthorizationException.fromIntent(getIntent());
            mAuthState = new AuthState(response, ex);

            if (response != null) {
                Timber.d("Received AuthorizationResponse.");
                showSnackBar(getString(R.string.exchange_notification));
                String clientSecret = getClientSecretFromIntent(getIntent());
                if (clientSecret != null) {
                    exchangeAuthorizationCode(response, new ClientSecretBasic(clientSecret));
                } else {
                    exchangeAuthorizationCode(response);
                }
            } else {
                Timber.i("Authorization failed: " + ex);
                showSnackBar(getString(R.string.authorization_failed));
                // todo logout(); has to be called
            }
        }

        setUp();

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
        showSnackBar((tokenResponse != null)
                ? getString(R.string.exchange_complete)
                : getString(R.string.refresh_failed));

        // for updating the access token
        if (tokenResponse != null) {
            mPresenter.updateAccessToken(tokenResponse.accessToken);
            //setting the user loggedIn mode for
            mPresenter.setLoggedInMode();// todo get back here later

            // for updating the UI
            refreshUi();
        }


    }

    private void performTokenRequest(TokenRequest request) {
        performTokenRequest(request, NoClientAuthentication.INSTANCE);
    }

    // to be executed only when there is an internet connection.
    @SuppressLint("StaticFieldLeak")
    private void fetchUserInfo() {
        if (mAuthState.getAuthorizationServiceConfiguration() == null) {
            Timber.e("Cannot make userInfo request without service configuration");
        }

        mAuthState.performActionWithFreshTokens(mAuthService, (accessToken, idToken, ex) -> {
            if (ex != null) {
                Timber.e("Token refresh failed when fetching user info");
                return;
            }

            AuthorizationServiceDiscovery discoveryDoc = getDiscoveryDocFromIntent(getIntent());
            if (discoveryDoc == null) {
                throw new IllegalStateException("no available discovery doc");
            }

            URL userInfoEndpoint;
            if (discoveryDoc.getUserinfoEndpoint() != null) {
                try {
                    userInfoEndpoint = new URL(discoveryDoc.getUserinfoEndpoint().toString());
                } catch (MalformedURLException urlEx) {
                    Timber.e(urlEx, "Failed to construct user info endpoint URL");
                    return;
                }

                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        InputStream userInfoResponse = null;
                        try {
                            Log.e("~~~~~", accessToken);
                            HttpURLConnection conn = (HttpURLConnection) userInfoEndpoint.openConnection();
                            conn.setRequestProperty("Authorization", "Bearer " + accessToken);
                            conn.setInstanceFollowRedirects(false);
                            userInfoResponse = conn.getInputStream();
                            String response = readStream(userInfoResponse);
                            updateUserInfo(new JSONObject(response));
                        } catch (IOException ioEx) {
                            Timber.e(ioEx, "Network error when querying userinfo endpoint");
                        } catch (JSONException jsonEx) {
                            Timber.e("Failed to parse userInfo response");
                        } finally {
                            if (userInfoResponse != null) {
                                try {
                                    userInfoResponse.close();
                                } catch (IOException ioEx) {
                                    Timber.e(ioEx, "Failed to close userInfo response stream");
                                }
                            }
                        }
                        return null;
                    }
                }.execute();

            }

        });
    }

    private void updateUserInfo(final JSONObject jsonObject) {
        new Handler(Looper.getMainLooper()).post(() -> {
            mUserInfoJson = jsonObject;
            refreshUi();
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        if (mAuthState != null) {
            state.putString(KEY_AUTH_STATE, mAuthState.jsonSerializeString());
        }

        if (mUserInfoJson != null) {
            state.putString(KEY_USER_INFO, mUserInfoJson.toString());
        }
    }

    @SuppressLint({"StaticFieldLeak", "RestrictedApi"})
    private void refreshUi() {

        if (mAuthState.isAuthorized()) {

            // user is authorized and has access token to do whatever he wishes to do since he is eligible
            fabSensor.setVisibility(View.VISIBLE);
            fabSensor.setOnClickListener(view -> mPresenter.onFabClicked());

            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                    .replace(R.id.flContent, DevicesFragment.newInstance(), DevicesFragment.TAG)
                    .commit();
        }

        // if the user is not authorized user
        AuthorizationServiceDiscovery discoveryDoc = getDiscoveryDocFromIntent(getIntent());
        if (!mAuthState.isAuthorized()
                || discoveryDoc == null
                || discoveryDoc.getUserinfoEndpoint() == null) {

            // todo something has to be inserted in here

//        } else {
//            fetchUserInfo();
            // if the user is not authorized then it shouldn't fetch instead tell the user to re-Authenticate
        }

        if (mUserInfoJson == null) {
            showMessage("user infoJson is null");
            // todo do something in here
        } else {

            try {

                // if the mUserInfoJson has an attribute called -> picture
                if (mUserInfoJson.has("picture")) {
                    int profilePictureSize =
                            getResources().getDimensionPixelSize(R.dimen.profile_pic_size);

                    Picasso.get()
                            .load(Uri.parse(mUserInfoJson.getString("picture")))
                            .resize(profilePictureSize, profilePictureSize)
                            .into(mProfileView);
                }

                // prints the user info
                Timber.e(mUserInfoJson.toString());

                // updates the user information
                mPresenter.updateUserInfo(
                        (mUserInfoJson.has("name")) ? mUserInfoJson.get("name").toString() : "",
                        (mUserInfoJson.has("preferred_username")) ? mUserInfoJson.get("preferred_username").toString() : "",
                        (mUserInfoJson.has("given_name")) ? mUserInfoJson.get("given_name").toString() : "",
                        (mUserInfoJson.has("family_name")) ? mUserInfoJson.get("family_name").toString() : "",
                        (mUserInfoJson.has("email")) ? mUserInfoJson.get("email").toString() : "");

            } catch (JSONException ext) {
                Timber.e(ext, "Failed to read userInfo JSON");
            }
        }
    }

    private void logout() {

        if (mAuthState.getAuthorizationServiceConfiguration() == null) {
            Timber.e("Cannot make userInfo request without service configuration");
        }

        mAuthState.performActionWithFreshTokens(mAuthService, (accessToken, idToken, ex) -> {
            if (ex != null) {
                Timber.e("Token refresh failed when fetching user info");
                showSnackBar("Token refresh failed when fetching user info");
                return;
            }

            mPresenter.updateAccessToken(accessToken);

            AuthorizationServiceDiscovery discoveryDoc = getDiscoveryDocFromIntent(getIntent());
            if (discoveryDoc == null) {
                throw new IllegalStateException("no available discovery doc");
            }

            Uri endSessionEndpoint = Uri.parse(discoveryDoc.getEndSessionEndpoint().toString());

            String logoutUri = getResources().getString(R.string.keycloak_auth_logout_uri);
            LogoutRequest logoutRequest = new LogoutRequest(endSessionEndpoint,
                    Uri.parse(logoutUri));

            LogoutService logoutService = new LogoutService(MainActivity.this);
            logoutService.performLogoutRequest(
                    logoutRequest,
                    PendingIntent.getActivity(
                            MainActivity.this, logoutRequest.hashCode(),
                            new Intent(MainActivity.this, LoginActivity.class), 0)
            );
        });
    }

    @Override
    protected void setUp() {
        changeToolbarTitle(getString(R.string.devices));

        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawer,
                mToolbar,
                R.string.open_drawer,
                R.string.close_drawer) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                hideKeyboard(MainActivity.this);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        mDrawer.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        setupDrawerContent(nvDrawer);

        mPresenter.onNavMenuCreated();

    }

    private void setupDrawerContent(NavigationView navigationView) {
        View headerView = navigationView.getHeaderView(0);
        mEmailTextView = headerView.findViewById(R.id.tv_email);
        mNameTextView = headerView.findViewById(R.id.tv_name);
        mProfileView = headerView.findViewById(R.id.nav_profile);

        loadNavHeader();

        navigationView.setNavigationItemSelectedListener(
                menuItem -> {
                    // todo get back here later
//                    menuItem.setChecked(true);
                    selectDrawerItem(menuItem);
                    return true;
                });
    }

    @SuppressLint("StaticFieldLeak")
    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        Fragment fragment = null;
        Class fragmentClass = null;

        switch (menuItem.getItemId()) {
            case R.id.nav_sensor:
                fragmentClass = DevicesFragment.class;
                CURRENT_TAG = DevicesFragment.TAG;
                changeToolbarTitle(getString(R.string.devices));
                break;
            case R.id.nav_notification:
                fragmentClass = NotificationFragment.class;
                CURRENT_TAG = NotificationFragment.TAG;
                changeToolbarTitle(getString(R.string.notification));
                break;
            case R.id.nav_map:
                getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                        .replace(R.id.cl_root_view, MapFragment.newInstance(), MapFragment.TAG)
                        .commit();

//                fragmentClass = MapFragment.class;
//                CURRENT_TAG = MapFragment.TAG;
//                changeToolbarTitle(getString(R.string.map));
                break;
            case R.id.nav_setting:
                CommonUtils.toast("settings clicked");
                CURRENT_TAG = DevicesFragment.TAG;
                break;
            case R.id.nav_logout:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.are_you_sure_you_want_to_logout)
                        .setPositiveButton(getString(R.string.logout), (dialog, id) -> {

                            mPresenter.onLogOutClicked();
//                            signOut();
                            //todo get back here and resolve this
                            new AsyncTask<Void, Void, Void>() {
                                @Override
                                protected Void doInBackground(Void... params) {
                                    logout();
                                    return null;
                                }
                            }.execute();

                        })
                        .setNegativeButton(getString(R.string.cancel), (dialog, id) -> {
                            dialog.dismiss();
                        });
                AlertDialog alert = builder.create();
                alert.show();
                break;
            default:
                fragmentClass = DevicesFragment.class;
        }

        try {
            if (fragmentClass != null)
                fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            mDrawer.closeDrawers();
            return;
        }

        Fragment finalFragment = fragment;
        Runnable mPendingRunnable = () -> {
            // Insert the fragment by replacing any existing fragment
            FragmentManager fragmentManager = getSupportFragmentManager();
            if (finalFragment != null)
                fragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                        .replace(R.id.flContent, finalFragment, CURRENT_TAG)
                        .commit();
        };

        mHandler.post(mPendingRunnable);

        // Set action bar title
        setTitle(menuItem.getTitle());
        // Close the navigation drawer
        mDrawer.closeDrawers();
    }

    public void changeToolbarTitle(String title) {
        mToolbar.setTitle(String.valueOf(title));
        setSupportActionBar(mToolbar);
        if (title.equals(getString(R.string.map))) {
            if (getSupportActionBar() != null)
                getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.color.transparent));
        } else {
            if (getSupportActionBar() != null)
                getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.color.colorPrimary));
        }
    }

    @Override
    public void onBackPressed() {
        Timber.e("onBackPressed");
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawers();
            return;
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(DevicesFragment.TAG);
        if (fragment == null) {
            new AlertDialog.Builder(this)
                    .setMessage(getString(R.string.are_you_sure_you_want_to_exit))
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.cancel), (dialog, which) -> dialog.dismiss())
                    .setNegativeButton(getString(R.string.yes), (dialog, which) -> super.onBackPressed())
                    .show();
        } else {
            Timber.e(String.valueOf(fragment.getTag()));
            getSupportFragmentManager()
                    .beginTransaction()
                    .remove(fragment)
                    .commitNow();

            unlockDrawer();
        }
    }

    @Override
    public void onFragmentDetached(String tag, String parent) {
        Timber.e("onFragmentDetached");
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(tag);
        if (fragment != null) {
            fragmentManager
                    .beginTransaction()
                    .remove(fragment)
                    .commitNow();
            unlockDrawer();

            if (TextUtils.equals(parent, MapFragment.TAG)) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                        .replace(R.id.flContent, MapFragment.newInstance(), MapFragment.TAG)
                        .commit();
            } else if (TextUtils.equals(parent, DevicesFragment.TAG)) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                        .replace(R.id.flContent, DevicesFragment.newInstance(), DevicesFragment.TAG)
                        .commit();
            }
        }
    }

    private void loadNavHeader() {
//        mNameTextView.setText("Corentin Dupont");
//        mEmailTextView.setText("test@gmail.com");

        // showing dot next to notifications label
        nvDrawer.getMenu().getItem(1).setActionView(R.layout.menu_dot);
    }

    @Override
    public void updateUserName(String currentUserName) {
        mNameTextView.setText(currentUserName);
    }

    @Override
    public void updateUserEmail(String currentUserEmail) {
        mEmailTextView.setText(currentUserEmail);
    }

    @Override
    public void updateUserProfilePic(String currentUserProfilePicUrl) {
        //load profile pic url into ANImageView
        Picasso.get()
                .load(currentUserProfilePicUrl)
                .placeholder(R.drawable.ic_account)
                .into(mProfileView);
    }

    @Override
    public void openLoginActivity() {
        startActivity(LoginActivity.getStartIntent(MainActivity.this));
        finish();
    }

    @Override
    public void openRegistrationSensor() {
        lockDrawer();
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                .replace(R.id.cl_root_view, RegisterSensorFragment.newInstance(), RegisterSensorFragment.TAG)
                .commit();
    }

    @Override
    public void openSensorDetailFragment(Device device, String parentFragment) {
        lockDrawer();
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)//adding animation
                .replace(R.id.cl_root_view, DetailDevicesFragment.newInstance(device, parentFragment), DetailDevicesFragment.TAG)
                .commit();
    }

    @Override
    public void lockDrawer() {
        if (mDrawer != null)
            mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    @Override
    public void unlockDrawer() {
        if (mDrawer != null)
            mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Drawable drawable = item.getIcon();
        if (drawable instanceof Animatable) {
            ((Animatable) drawable).start();
        }
        if (item.getItemId() == android.R.id.home) {
            mDrawer.openDrawer(GravityCompat.START);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideLoading();
        if (mAuthService != null) {
            mAuthService.dispose();
        }
    }

    @Override
    public void onConfigurationChanged(android.content.res.Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onItemClicked(Device device) {
        mPresenter.onSensorItemClicked(device, DevicesFragment.TAG);
    }

    @Override
    public void showFab() {
        fabSensor.show();
    }

    @Override
    public void hideFab() {
        fabSensor.hide();
    }

    @Override
    public boolean isFabShown() {
        return fabSensor.isShown();
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void visibleFab() {
        CoordinatorLayout.LayoutParams p = (CoordinatorLayout.LayoutParams) fabSensor.getLayoutParams();
        p.setBehavior(new FloatingActionButton.Behavior());
        p.setAnchorId(R.id.flContent);
        fabSensor.setLayoutParams(p);
        fabSensor.setVisibility(View.VISIBLE);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void invisibleFab() {

        CoordinatorLayout.LayoutParams p = (CoordinatorLayout.LayoutParams) fabSensor.getLayoutParams();
        p.setBehavior(null); //should disable default animations
        p.setAnchorId(View.NO_ID); //should let you set visibility
        fabSensor.setLayoutParams(p);
        fabSensor.setVisibility(View.GONE);
    }

    @Override
    public void onMarkerClicked(Device device, String parentFragment) {
        openSensorDetailFragment(device, parentFragment);
    }
}
