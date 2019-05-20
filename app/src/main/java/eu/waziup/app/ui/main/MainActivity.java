package eu.waziup.app.ui.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
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

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.waziup.app.BuildConfig;
import eu.waziup.app.R;
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
import eu.waziup.app.utils.CommonUtils;

public class MainActivity extends BaseActivity implements MainMvpView, SensorCommunicator, MapCommunicator {

    private static final String SHARED_PREFERENCES_NAME = "AuthStatePreference";
    private static final String AUTH_STATE = "AUTH_STATE";
    public static String CURRENT_TAG = SensorFragment.TAG;
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

    public static Intent getStartIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, BuildConfig.MAPBOX_TOKEN);
        AndroidThreeTen.init(this);
        setContentView(R.layout.activity_main);

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
    }

    @Override
    protected void onResume() {
        super.onResume();
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
//                            mAuth.signOut();
                            mPresenter.onLogOutClicked();
                            Log.e("--->logut", "clicked");
                            // Google revoke access && singOut -> This is best practice. Though not required
//                            mGoogleSignInClient.revokeAccess();
//                            mGoogleSignInClient.signOut();
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
                getSupportFragmentManager()
                        .beginTransaction()
                        .remove(fragment)
                        .commitNow();

                unlockDrawer();
            }
        }
    }

    @Override
    public void onFragmentDetached(String tag, String parent) {
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
    public void onConfigurationChanged(Configuration newConfig) {
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
