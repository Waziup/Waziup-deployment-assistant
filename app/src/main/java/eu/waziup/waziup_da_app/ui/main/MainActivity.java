package eu.waziup.waziup_da_app.ui.main;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.Objects;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.waziup.waziup_da_app.R;
import eu.waziup.waziup_da_app.data.network.model.sensor.Sensor;
import eu.waziup.waziup_da_app.ui.base.BaseActivity;
import eu.waziup.waziup_da_app.ui.detail.DetailSensorFragment;
import eu.waziup.waziup_da_app.ui.login.LoginActivity;
import eu.waziup.waziup_da_app.ui.map.MapFragment;
import eu.waziup.waziup_da_app.ui.register.RegisterSensorFragment;
import eu.waziup.waziup_da_app.ui.sensor.SensorCommunicator;
import eu.waziup.waziup_da_app.ui.sensor.SensorFragment;
import eu.waziup.waziup_da_app.utils.CommonUtils;

public class MainActivity extends BaseActivity implements MainMvpView, SensorCommunicator {

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

    public static Intent getStartIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getActivityComponent().inject(this);
        setUnBinder(ButterKnife.bind(this));
        mPresenter.onAttach(MainActivity.this);

        setUp();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.flContent, SensorFragment.newInstance(), SensorFragment.TAG)
                .commit();
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
                break;
            case R.id.nav_gateway:
                CommonUtils.toast("gateway clicked");
//                fragmentClass = DetailSensorFragment.class;
                break;
            case R.id.nav_notification:
                CommonUtils.toast("notification clicked");
//                fragmentClass = RegisterSensorMvpView.class;
                break;
            case R.id.nav_map:
                fragmentClass = MapFragment.class;
                break;
            case R.id.nav_setting://todo remove if nothing goes in here
                fragmentClass = MapFragment.class;
                CommonUtils.toast("settings clicked");
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

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragment != null)
            fragmentManager.beginTransaction()
                    .replace(R.id.flContent, fragment)
                    .commit();

        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);
        // Set action bar title
        setTitle(menuItem.getTitle());
        // Close the navigation drawer
        mDrawer.closeDrawers();
    }

    @Override
    public void onBackPressed() {
        // todo there has to be some way of handling the nullPointerException
        mDrawerToggle.setDrawerIndicatorEnabled(true);

        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            Objects.requireNonNull(Objects.requireNonNull(getSupportFragmentManager()
                    .findFragmentByTag(SensorFragment.TAG))
                    .getView())
                    .findViewById(R.id.fab_sensor)
                    .setVisibility(View.VISIBLE);
        }

        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
//            onFragmentDetached();
        } else {
            super.onBackPressed();
        }


//        FragmentManager fragmentManager = getSupportFragmentManager();
//        Fragment fragment = fragmentManager.findFragmentByTag(RegisterSensorFragment.TAG);
//        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
//            mDrawer.closeDrawer(GravityCompat.START);
//        } else if (fragment == null) {
//            super.onBackPressed();
//        } else {
//            if (fragment instanceof RegisterSensorFragment)
//                onFragmentDetached(RegisterSensorFragment.TAG);
//            if (fragment instanceof DetailSensorFragment)
//                onFragmentDetached(DetailSensorFragment.TAG);
//        }
    }

    @Override
    public void onFragmentDetached(String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(tag);
        if (fragment != null) {
            fragmentManager
                    .beginTransaction()
                    .disallowAddToBackStack()
                    .remove(fragment)
                    .commitNow();
            unlockDrawer();
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
    public void openRegisterationSensor() {
        lockDrawer();
        mDrawerToggle.setDrawerIndicatorEnabled(false);
        if (getSupportFragmentManager().findFragmentByTag(SensorFragment.TAG) != null)
            Objects.requireNonNull(Objects.requireNonNull(getSupportFragmentManager().findFragmentByTag(SensorFragment.TAG))
                    .getView())
                    .findViewById(R.id.fab_sensor).setVisibility(View.GONE);

        getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack(null)
                .add(R.id.cl_root_view, RegisterSensorFragment.newInstance(), RegisterSensorFragment.TAG)
                .commit();
    }

    @Override
    public void openSensorDetailFragment(Sensor sensor) {
        lockDrawer();
        mDrawerToggle.setDrawerIndicatorEnabled(false);
        if (getSupportFragmentManager().findFragmentByTag(SensorFragment.TAG) != null)
            Objects.requireNonNull(Objects.requireNonNull(getSupportFragmentManager().findFragmentByTag(SensorFragment.TAG))
                    .getView())
                    .findViewById(R.id.fab_sensor).setVisibility(View.GONE);

        getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack(null)
                .add(R.id.cl_root_view, DetailSensorFragment.newInstance(sensor), DetailSensorFragment.TAG)
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
        mPresenter.onSensorItemClicked(sensor);

    }
}
