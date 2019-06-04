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
import android.support.annotation.MainThread;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
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
import android.widget.Toast;

import com.jakewharton.threetenabp.AndroidThreeTen;
import com.mapbox.mapboxsdk.Mapbox;
import com.squareup.picasso.Picasso;

import net.openid.appauth.AppAuthConfiguration;
import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.AuthorizationService;
import eu.waziup.app.data.network.model.logout.AuthorizationServiceDiscovery;

import net.openid.appauth.TokenResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.waziup.app.BuildConfig;
import eu.waziup.app.R;
import eu.waziup.app.data.network.model.logout.LogoutRequest;
import eu.waziup.app.data.network.model.logout.LogoutService;
import eu.waziup.app.data.network.model.sensor.Sensor;
import eu.waziup.app.ui.base.BaseActivity;
import eu.waziup.app.ui.custom.RoundedImageView;
import eu.waziup.app.ui.login.LoginActivity;
import eu.waziup.app.ui.map.MapCommunicator;
import eu.waziup.app.ui.map.MapFragment;
import eu.waziup.app.ui.notification.NotificationFragment;
import eu.waziup.app.ui.register.RegisterSensorFragment;
import eu.waziup.app.ui.sensor.SensorCommunicator;
import eu.waziup.app.ui.sensor.SensorFragment;
import eu.waziup.app.ui.sensordetail.DetailSensorFragment;
import eu.waziup.app.utils.AuthStateManager;
import eu.waziup.app.utils.CommonUtils;
import eu.waziup.app.utils.Configuration;
import okio.Okio;

public class MainActivity extends BaseActivity implements MainMvpView, SensorCommunicator, MapCommunicator {

    public static final String TAG = MainActivity.class.getSimpleName();
    private static final String SHARED_PREFERENCES_NAME = "AuthStatePreference";
    private static final String AUTH_STATE = "AUTH_STATE";

    private static final String EXTRA_AUTH_SERVICE_DISCOVERY = "authServiceDiscovery";

    // AUTHORIZATION VARIABLES
    private static final String KEY_USER_INFO = "userInfo";
    public static String CURRENT_TAG = SensorFragment.TAG;
    private final AtomicReference<JSONObject> mUserInfoJson = new AtomicReference<>();
    @Inject
    MainMvpPresenter<MainMvpView> mPresenter;
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
    private Handler mHandler;
    private AuthorizationService mAuthService;
    private AuthStateManager mStateManager;
    private Configuration mConfiguration;


    public static Intent getStartIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, BuildConfig.MAPBOX_TOKEN);
        AndroidThreeTen.init(this);

        Log.e(TAG, "---> mainActivity");

        mStateManager = AuthStateManager.getInstance(this);
        mConfiguration = Configuration.getInstance(this);

        Configuration config = Configuration.getInstance(this);
        mAuthService = new AuthorizationService(this, new AppAuthConfiguration.Builder()
                .setConnectionBuilder(config.getConnectionBuilder())
                .build());

        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            try {
                mUserInfoJson.set(new JSONObject(savedInstanceState.getString(KEY_USER_INFO)));
            } catch (JSONException ex) {
                Log.e(TAG, "Failed to parse saved user info JSON, discarding", ex);
            }
        }

        mHandler = new Handler();

        getActivityComponent().inject(this);
        setUnBinder(ButterKnife.bind(this));
        mPresenter.onAttach(MainActivity.this);

        setUp();

        fabSensor.setOnClickListener(view -> mPresenter.onFabClicked());

        // todo check the saved instance state before opening the sensorFragment
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                .replace(R.id.flContent, SensorFragment.newInstance(), SensorFragment.TAG)
                .commit();


    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.e(TAG, "isAuthorized=> " + mStateManager.getCurrent().isAuthorized());

        if (mStateManager.getCurrent().isAuthorized()) {
            // todo handle this later -> ############
            AuthState state = mStateManager.getCurrent();

            Log.e(TAG, String.valueOf(state.getAccessToken()));
            Log.e(TAG, String.valueOf(state.getIdToken()));
            Log.e(TAG, String.valueOf(state.getRefreshToken()));
            return;
        }

        // the stored AuthState is incomplete, so check if we are currently receiving the result of
        // the authorization flow from the browser.
        AuthorizationResponse response = AuthorizationResponse.fromIntent(getIntent());
        AuthorizationException ex = AuthorizationException.fromIntent(getIntent());

        if (response != null || ex != null) {
            mStateManager.updateAfterAuthorization(response, ex);
        }

        if (response != null && response.authorizationCode != null) {
            // authorization code exchange is required
            mStateManager.updateAfterAuthorization(response, ex);
            exchangeAuthorizationCode(response);
        } else if (ex != null) {
            signOut();
            // todo display something to the user telling them what goes wrong.
//            displayNotAuthorized("Authorization flow failed: " + ex.getMessage());
        } else {
            signOut();
//            displayNotAuthorized("No authorization state retained - reauthorization required");
        }

    }

    @MainThread
    private void exchangeAuthorizationCode(AuthorizationResponse authorizationResponse) {
//        displayLoading("Exchanging authorization code");
        Log.e(TAG, String.format("----->Token %s", authorizationResponse.accessToken));
        mAuthService.performTokenRequest(
                authorizationResponse.createTokenExchangeRequest(),
                this::handleCodeExchangeResponse);
    }

    @WorkerThread
    private void handleCodeExchangeResponse(@Nullable TokenResponse tokenResponse,
                                            @Nullable AuthorizationException authException) {

        mStateManager.updateAfterTokenResponse(tokenResponse, authException);
        if (!mStateManager.getCurrent().isAuthorized()) {
            final String message = "Authorization Code exchange failed"
                    + ((authException != null) ? authException.error : "");

            if (authException != null) {
                Log.e(TAG, String.valueOf(authException.error));
                Log.e(TAG, String.valueOf(authException.errorDescription));
                Log.e(TAG, String.valueOf(authException.errorUri));
                Log.e(TAG, String.valueOf(authException.code));
            }

            // WrongThread inference is incorrect for lambdas
            //noinspection WrongThread
            // todo handle this
            Log.e(TAG, message);

            runOnUiThread(this::signOut);
        } else {
            // todo handle this
            Toast.makeText(this, "fetching user information", Toast.LENGTH_SHORT).show();
//            runOnUiThread(this::fetchUserInfo);
        }

    }

    @MainThread
    private void fetchUserInfo() {
//        displayLoading("Fetching user info");
//        mStateManager.getCurrent().performActionWithFreshTokens(mAuthService, this::fetchUserInfo);
    }

//    @SuppressLint("StaticFieldLeak")
//    @MainThread
//    private void fetchUserInfo(String accessToken, String idToken, AuthorizationException ex) {
//
//        Log.e(TAG, String.format("token-idToken-> %s", idToken));
//        Log.e(TAG, String.format("token-accessToken-> %s", accessToken));
//        if (ex != null) {
//            Log.e(TAG, "Token refresh failed when fetching user info");
//            mUserInfoJson.set(null);
//            Toast.makeText(this, mUserInfoJson.toString(), Toast.LENGTH_SHORT).show();
////            runOnUiThread(this::displayAuthorized);
//            return;
//        }
//
//        AuthorizationServiceDiscovery discovery =
//                mStateManager.getCurrent()
//                        .getAuthorizationServiceConfiguration()
//                        .discoveryDoc;
//
//        URL userInfoEndpoint;
//        try {
//            userInfoEndpoint =
//                    mConfiguration.getUserInfoEndpointUri() != null
//                            ? new URL(mConfiguration.getUserInfoEndpointUri().toString())
//                            : new URL(discovery.getUserinfoEndpoint().toString());
//        } catch (MalformedURLException urlEx) {
//            Log.e(TAG, "Failed to construct user info endpoint URL", urlEx);
//            mUserInfoJson.set(null);
////            runOnUiThread(this::displayAuthorized);
//            return;
//        }
//
//        new AsyncTask<Void, Void, Void>() {
//            @Override
//            protected Void doInBackground(Void... voids) {
////String name, String preferredName, String givenName, String familyName, String email
//                try {
//                    HttpURLConnection conn =
//                            (HttpURLConnection) userInfoEndpoint.openConnection();
//                    conn.setRequestProperty("Authorization", "Bearer " + accessToken);
//                    conn.setInstanceFollowRedirects(false);
//                    String response = Okio.buffer(Okio.source(conn.getInputStream()))
//                            .readString(Charset.forName("UTF-8"));
//                    mUserInfoJson.set(new JSONObject(response));
//                    Log.e(TAG, "mUserInfoJson: " + mUserInfoJson);
//                    //String name, String preferredName, String givenName, String familyName, String email
//                    mPresenter.updateUserInfo(mUserInfoJson.get().get("name").toString(), mUserInfoJson.get().get("preferredName").toString(),
//                            mUserInfoJson.get().get("givenName").toString(), mUserInfoJson.get().get("familyName").toString(),
//                            mUserInfoJson.get().get("email").toString());
//                } catch (IOException ioEx) {
//                    Log.e(TAG, "Network error when querying userinfo endpoint", ioEx);
////                    CommonUtils.toast("Fetching user info failed");
//                } catch (JSONException jsonEx) {
//                    Log.e(TAG, "Failed to parse userinfo response");
////                    CommonUtils.toast("Failed to parse user info");
//                }
//
//                return null;
//            }
//        }.execute();
//
////        mExecutor.submit(() -> {
//
////            runOnUiThread(this::displayAuthorized);
////        });
//    }

    @MainThread
    private void signOut() {
        // discard the authorization and token state, but retain the configuration and
        // dynamic client registration (if applicable), to save from retrieving them again.
        AuthState currentState = mStateManager.getCurrent();
        AuthState clearedState =
                new AuthState(currentState.getAuthorizationServiceConfiguration());
        if (currentState.getLastRegistrationResponse() != null) {
            clearedState.update(currentState.getLastRegistrationResponse());
        }
        mStateManager.replace(clearedState);

        Intent mainIntent = new Intent(this, LoginActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    private void logout() {
        // todo handle mAuthState NullPointerException here
        if (mStateManager.getCurrent().getAuthorizationServiceConfiguration() == null) {
            Log.e(TAG, "Cannot make userInfo request without service configuration");
        }

        mStateManager.getCurrent().performActionWithFreshTokens(mAuthService, (accessToken, idToken, ex) -> {
            if (ex != null) {// todo it has to logout the user if the there is an exception happing in here
                Log.e(TAG, "Token refresh failed when fetching user info");
                signOut();// todo will be removed and replaced with another method
                return;
            }

            // todo find a solution for the discoveryDocument thing later
//            AuthorizationServiceDiscovery discoveryDoc = getDiscoveryDocFromIntent(getIntent());
//            if (discoveryDoc == null) {
//                throw new IllegalStateException("no available discovery doc");
//            }

            //============================ for clearing the state ==================================
            AuthState currentState = mStateManager.getCurrent();
            AuthState clearedState =
                    new AuthState(currentState.getAuthorizationServiceConfiguration());
            if (currentState.getLastRegistrationResponse() != null) {
                clearedState.update(currentState.getLastRegistrationResponse());
            }
            mStateManager.replace(clearedState);
            //======================================================================================

            //https://keycloak.staging.waziup.io/auth/realms/waziup/protocol/openid-connect/token
            Uri endSessionEndpoint = Uri.parse("https://keycloak.staging.waziup.io/auth/realms/waziup/protocol/openid-connect/logout");

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

    static AuthorizationServiceDiscovery getDiscoveryDocFromIntent(Intent intent) {
        if (!intent.hasExtra(EXTRA_AUTH_SERVICE_DISCOVERY)) {
            return null;
        }
        String discoveryJson = intent.getStringExtra(EXTRA_AUTH_SERVICE_DISCOVERY);
        try {
            return new AuthorizationServiceDiscovery(new JSONObject(discoveryJson));
        } catch (JSONException | AuthorizationServiceDiscovery.MissingArgumentException  ex) {
            throw new IllegalStateException("Malformed JSON in discovery doc");
        }
    }

    @Override
    protected void setUp() {
        changeToolbarTitle(getString(R.string.sensors));

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
                fragmentClass = SensorFragment.class;
                CURRENT_TAG = SensorFragment.TAG;
                changeToolbarTitle(getString(R.string.sensors));
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
                CURRENT_TAG = SensorFragment.TAG;
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
                fragmentClass = SensorFragment.class;
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

        // If mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }


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
        Log.e("--->", "onBackPressed");
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawers();
            return;
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(SensorFragment.TAG);
        if (fragment == null) {
            new AlertDialog.Builder(this)
                    .setMessage(getString(R.string.are_you_sure_you_want_to_exit))
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.cancel), (dialog, which) -> dialog.dismiss())
                    .setNegativeButton(getString(R.string.yes), (dialog, which) -> super.onBackPressed())
                    .show();
        } else {
            if (fragment != null) {
                Log.e("---->backPressed", "fragment != null");
                Log.e("==>backPressed", String.valueOf(fragment.getTag()));
                getSupportFragmentManager()
                        .beginTransaction()
                        .remove(fragment)
                        .commitNow();

                unlockDrawer();

//                    if (TextUtils.equals(parent, MapFragment.TAG)) {
//                        getSupportFragmentManager()
//                                .beginTransaction()
//                                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
//                                .replace(R.id.flContent, MapFragment.newInstance(), MapFragment.TAG)
//                                .commit();
//                    } else if (TextUtils.equals(parent, SensorFragment.TAG)) {
//                        getSupportFragmentManager()
//                                .beginTransaction()
//                                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
//                                .replace(R.id.flContent, SensorFragment.newInstance(), SensorFragment.TAG)
//                                .commit();
//                    }
            }
        }

//        if (getSupportFragmentManager().getFragments().size() > 1) {
//            Log.e("---->backPressed", "> 1");
//            FragmentManager fragmentManager = getSupportFragmentManager();
//            Fragment fragment = fragmentManager.findFragmentByTag(getSupportFragmentManager()
//                    .getFragments().get(getSupportFragmentManager().getFragments().size() - 1).getTag());
//
//            // this is like popping out the top fragment on the fragment stack list
//
//            }
//
//            unlockDrawer();
//        } else {
//
//            FragmentManager fragmentManager = getSupportFragmentManager();
//            Fragment fragment = fragmentManager.findFragmentByTag(getSupportFragmentManager()
//                    .getFragments().get(getSupportFragmentManager().getFragments().size() - 1).getTag());
//            if (fragment != null) {
//                Log.e("==>backPressed", String.valueOf(fragment.getTag()));
//            }
//
////            Log.e("---->backPressed", "<= 1");
////            Log.e("---->back Size", String.valueOf(getSupportFragmentManager().getFragments().size()));
////            Fragment f = getSupportFragmentManager().findFragmentById(R.id.layout_container);
////            if(f instanceof SensorFragment){
//
//            if (getSupportFragmentManager().getFragments().size() <= 1) {
////                SensorFragment sensorFragment = (SensorFragment) getSupportFragmentManager().findFragmentByTag(SensorFragment.TAG);
//                if (Objects.equals(getSupportFragmentManager().getFragments().get(0).getTag(), SensorFragment.TAG)) {// && sensorFragment.isVisible()) {
//                    Log.e("---->backPressed", String.valueOf(getSupportFragmentManager().getFragments().get(0).getTag()));
//
//                }
//            } else {
//                Log.e("---->backPressed", "else");
//                // if the opened fragment is beside the sensorFragment which is the home fragment
//                getSupportFragmentManager()
//                        .beginTransaction()
//                        .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
//                        .replace(R.id.flContent, SensorFragment.newInstance(), SensorFragment.TAG)
//                        .commit();
//            }
//        }
        // todo check for the significance of this statement
    }

    @Override
    public void onFragmentDetached(String tag, String parent) {
        Log.e("--->", "onFragmentDetached");
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
            } else if (TextUtils.equals(parent, SensorFragment.TAG)) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                        .replace(R.id.flContent, SensorFragment.newInstance(), SensorFragment.TAG)
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
    public void openSensorDetailFragment(Sensor sensor, String parentFragment) {
        lockDrawer();
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)//adding animation
                .replace(R.id.cl_root_view, DetailSensorFragment.newInstance(sensor, parentFragment), DetailSensorFragment.TAG)
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
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawer.openDrawer(GravityCompat.START);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
    public void onItemClicked(Sensor sensor) {
        mPresenter.onSensorItemClicked(sensor, SensorFragment.TAG);
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
    public void onMarkerClicked(Sensor sensor, String parentFragment) {
        openSensorDetailFragment(sensor, parentFragment);
    }
}
