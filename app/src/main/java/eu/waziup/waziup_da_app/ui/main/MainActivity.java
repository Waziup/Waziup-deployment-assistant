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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.waziup.waziup_da_app.R;
import eu.waziup.waziup_da_app.ui.base.BaseActivity;
import eu.waziup.waziup_da_app.ui.detail.DetailSensorFragment;
import eu.waziup.waziup_da_app.ui.login.LoginActivity;
import eu.waziup.waziup_da_app.ui.register.RegisterSensorMvpView;
import eu.waziup.waziup_da_app.ui.sensor.SensorFragment;
import eu.waziup.waziup_da_app.utils.CommonUtils;

public class MainActivity extends BaseActivity implements MainMvpView {

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

//        FragmentManager fragmentManager = getSupportFragmentManager();
//        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.flContent, SensorFragment.newInstance(), SensorFragment.TAG)
                .commit();

//        fragmentManager = getSupportFragmentManager();
//
//        fragmentManager.beginTransaction()
//                .add(R.id.container, new ND1Fragment())
//                .commit();

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
        Class fragmentClass;
        switch (menuItem.getItemId()) {
            case R.id.nav_sensor:
                fragmentClass = SensorFragment.class;
                break;
            case R.id.nav_gateway:
                fragmentClass = DetailSensorFragment.class;
                break;
            case R.id.nav_notification:
                fragmentClass = RegisterSensorMvpView.class;
                break;
//            case R.id.nav_map:
//                fragmentClass = ThirdFragment.class;
//                break;
            default:
                fragmentClass = SensorFragment.class;
        }

        try {
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

//    void setupNavMenu() {
//        View headerLayout = mNavigationView.getHeaderView(0);
////        mProfileImageView = (RoundedImageView) headerLayout.findViewById(R.id.iv_profile_pic);
//        mNameTextView = headerLayout.findViewById(R.id.tv_name);
//        mEmailTextView = headerLayout.findViewById(R.id.tv_email);
//
//        mNavigationView.setNavigationItemSelectedListener(
//                item -> {
//                    mDrawer.closeDrawer(GravityCompat.START);
//                    switch (item.getItemId()) {
//                        case R.id.nav_notices:
//                            mPresenter.openNotices();
//                            return true;
//                        case R.id.nav_meetings:
//                            mPresenter.openMeetings();
//                            return true;
//                        case R.id.nav_archives_meetings:
//                            mPresenter.openArchivesMeetings();
//                            return true;
//                        case R.id.nav_archives_document:
//                            mPresenter.openArchivesDocuments();
//                            return true;
//                        case R.id.nav_question_answer:
//                            mPresenter.openQuestionAnswer();
//                            return true;
//                        case R.id.nav_settings:
//                            mPresenter.openSettings();
//                            return true;
//                        case R.id.nav_about:
//                            showAboutFragment();
//                            return true;
//                        case R.id.nav_logout:
//                            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//                            builder.setMessage(R.string.dialog_logout)
//                                    .setPositiveButton(R.string.logout, (dialog, id) -> {
//                                        mPresenter.onDrawerOptionLogoutClick();
//                                    })
//                                    .setNegativeButton(R.string.cancel, (dialog, id) -> {
//                                        dialog.dismiss();
//                                    });
//                            AlertDialog alert = builder.create();
//                            alert.show();
//                            return true;
//                        default:
//                            return false;
//                    }
//                });
//    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

//    todo get back here later
//    @Override
//    public void onBackPressed() {
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        Fragment fragment = fragmentManager.findFragmentByTag(AboutFragment.TAG);
//        if (fragment == null) {
//            super.onBackPressed();
//        } else {
//            onFragmentDetached(AboutFragment.TAG);
//        }
//    }

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

//    @Override
//    public void openRegistrationFragment() {
//        lockDrawer();
//        getSupportFragmentManager()
//                .beginTransaction()
//                .disallowAddToBackStack()
//                .add(R.id.flContent, RegisterSensorFragment.newInstance(), RegisterSensorFragment.TAG)
//                .commit();
//    }

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
    public void openNotificationFragment() {

    }

    @Override
    public void openGatewayFragment() {

    }

    @Override
    public void openMapFragment() {

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
            case R.id.menu_logout:
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
                return true;
            case R.id.menu_settings://todo has to be included in the navigation drawer
                CommonUtils.toast("settings clicked");
                return true;
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
}
