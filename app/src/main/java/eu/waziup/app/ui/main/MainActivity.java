package eu.waziup.app.ui.main;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.mapbox.mapboxsdk.Mapbox;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.waziup.app.BuildConfig;
import eu.waziup.app.R;
import eu.waziup.app.data.network.model.sensor.Sensor;
import eu.waziup.app.ui.base.BaseActivity;
import eu.waziup.app.ui.detail.DetailSensorFragment;
import eu.waziup.app.ui.login.LoginActivity;
import eu.waziup.app.ui.map.MapCommunicator;
import eu.waziup.app.ui.map.MapFragment;
import eu.waziup.app.ui.register.RegisterSensorFragment;
import eu.waziup.app.ui.sensor.SensorCommunicator;
import eu.waziup.app.ui.sensor.SensorFragment;
import eu.waziup.app.utils.CommonUtils;

public class MainActivity extends BaseActivity implements MainMvpView, SensorCommunicator, MapCommunicator {

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

    private TextView mNameTextView;

    private TextView mEmailTextView;

    private ActionBarDrawerToggle mDrawerToggle;


    public static String CURRENT_TAG = SensorFragment.TAG;
    private Handler mHandler;

    public static Intent getStartIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, BuildConfig.MAPBOX_TOKEN);
        setContentView(R.layout.activity_main);

        mHandler = new Handler();

        getActivityComponent().inject(this);
        setUnBinder(ButterKnife.bind(this));
        mPresenter.onAttach(MainActivity.this);

        setUp();

        // todo check the saved instance state before opening the sensorFragment
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.flContent, SensorFragment.newInstance(), SensorFragment.TAG)
                .commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void setUp() {
        mToolbar.setTitle(R.string.app_name);
        setSupportActionBar(mToolbar);

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

        loadNavHeader();

        navigationView.setNavigationItemSelectedListener(
                menuItem -> {
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
                break;
//            case R.id.nav_gateway:
//                CommonUtils.toast("gateway clicked");
////                fragmentClass = DetailSensorFragment.class;
//                break;
            case R.id.nav_notification:
                CommonUtils.toast("notification clicked");
                CURRENT_TAG = SensorFragment.TAG;
                break;
            case R.id.nav_map:
                fragmentClass = MapFragment.class;
                CURRENT_TAG = MapFragment.TAG;
                break;
            case R.id.nav_setting:
                CommonUtils.toast("settings clicked");
                CURRENT_TAG = SensorFragment.TAG;
                break;
            case R.id.nav_logout:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Are you sure you want to logout?")
                        .setPositiveButton("Logout", (dialog, id) -> {
                            mPresenter.onLogOutClicked();
                        })
                        .setNegativeButton("Cancel", (dialog, id) -> {
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

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawers();
            return;
        }

        new AlertDialog.Builder(this)
//                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("WAZIUP")
                .setMessage("Are you sure you want to close WAZIUP?")
                .setPositiveButton("Yes", (dialog, which) -> finish())
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    public void onFragmentDetached(String tag, String parent) {
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(tag);
        if (fragment != null) {
            fragmentManager
                    .beginTransaction()
                    .disallowAddToBackStack()
                    .remove(fragment)
                    .commitNow();
            unlockDrawer();

            if (TextUtils.equals(parent, MapFragment.TAG)) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.flContent, MapFragment.newInstance(), MapFragment.TAG)
                        .commit();
            } else if (TextUtils.equals(parent, SensorFragment.TAG)) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.flContent, SensorFragment.newInstance(), SensorFragment.TAG)
                        .commit();
            }
        }

    }

    private void loadNavHeader() {
        // name, website
        // todo get the current user information from his "username"
        mNameTextView.setText("Corentin Dupont");
        mEmailTextView.setText("test@gmail.com");

        // showing dot next to notifications label
        nvDrawer.getMenu().getItem(1).setActionView(R.layout.menu_dot);
    }

    @Override
    public void onBackPressed(String tag, String parentFragment) {
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        FragmentManager fragmentManager = getSupportFragmentManager();

        Fragment fragment = fragmentManager.findFragmentByTag(DetailSensorFragment.TAG);
        if (fragment == null) {
            new AlertDialog.Builder(this)
//                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("WAZIUP")
                    .setMessage("Are you sure you want to close WAZIUP?")
                    .setPositiveButton("Yes", (dialog, which) -> finish())
                    .setNegativeButton("No", null)
                    .show();

        } else {
            onFragmentDetached(tag, parentFragment);
        }
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
    }

    @Override
    public void openLoginActivity() {
        startActivity(LoginActivity.getStartIntent(MainActivity.this));
        finish();
    }

    @Override
    public void openRegistrationSensor() {
        lockDrawer();
//        mDrawerToggle.setDrawerIndicatorEnabled(false);
//        if (getSupportFragmentManager().findFragmentByTag(SensorFragment.TAG) != null)
//            Objects.requireNonNull(Objects.requireNonNull(getSupportFragmentManager().findFragmentByTag(SensorFragment.TAG))
//                    .getView())
//                    .findViewById(R.id.fab_sensor).setVisibility(View.GONE);

        getSupportFragmentManager()
                .beginTransaction()
                .disallowAddToBackStack()
                .replace(R.id.cl_root_view, RegisterSensorFragment.newInstance(), RegisterSensorFragment.TAG)
                .commit();
    }

    @Override
    public void openSensorDetailFragment(Sensor sensor, String parentFragment) {
        lockDrawer();
//        mDrawerToggle.setDrawerIndicatorEnabled(false);
//
//        if (getSupportFragmentManager().findFragmentByTag(SensorFragment.TAG) != null)
//            Objects.requireNonNull(Objects.requireNonNull(getSupportFragmentManager().findFragmentByTag(SensorFragment.TAG))
//                    .getView())
//                    .findViewById(R.id.fab_sensor).setVisibility(View.GONE);

        getSupportFragmentManager()
                .beginTransaction()
                .disallowAddToBackStack()
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
    public void fabClicked() {
        mPresenter.onFabClicked();
    }

    @Override
    public void onItemClicked(Sensor sensor) {
        mPresenter.onSensorItemClicked(sensor, SensorFragment.TAG);
    }

    @Override
    public void onMarkerClicked(Sensor sensor, String parentFragment) {
        openSensorDetailFragment(sensor, parentFragment);
    }
}