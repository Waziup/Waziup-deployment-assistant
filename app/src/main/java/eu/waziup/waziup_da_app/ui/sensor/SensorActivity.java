package eu.waziup.waziup_da_app.ui.sensor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.OnClick;
import eu.waziup.waziup_da_app.R;
import eu.waziup.waziup_da_app.data.network.model.sensor.Sensor;
import eu.waziup.waziup_da_app.ui.base.BaseActivity;
import eu.waziup.waziup_da_app.ui.login.LoginActivity;
import eu.waziup.waziup_da_app.ui.register.RegisterSensorActivity;
import eu.waziup.waziup_da_app.utils.CommonUtils;
import eu.waziup.waziup_da_app.utils.Constants;

public class SensorActivity extends BaseActivity implements SensorMvpView {


    @Inject
    SensorMvpPresenter<SensorMvpView> mPresenter;

    RecyclerView mRecyclerView;
    SensorAdapter mAdapter;
    //    Toolbar mToolbar;
    List<Sensor> sensorList = new ArrayList<>();
    SharedPreferences sharedpreferences;
    SwipeRefreshLayout mSwipeRefreshLayout;
    FloatingActionButton mfab;

    public static Intent getStartIntent(Context context) {
        return new Intent(context, SensorActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

        getActivityComponent().inject(this);
        setUnBinder(ButterKnife.bind(this));
        mPresenter.onAttach(SensorActivity.this);

        setUp();

        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            mPresenter.loadSensors();
            mSwipeRefreshLayout.setRefreshing(false);
        });

        mfab.setOnClickListener(view -> {
            startActivity(new Intent(SensorActivity.this, RegisterSensorActivity.class));
        });
    }

    @OnClick(R.id.fab_sensor)
    void onFabClicked() {
        mPresenter.onRegisterFabClicked();
    }

    @Override
    protected void setUp() {
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle("Sensors");

        mRecyclerView = findViewById(R.id.sensor_recycler);
        mSwipeRefreshLayout = findViewById(R.id.sensor_swipe_to_refresh);
        mfab = findViewById(R.id.fab_sensor);

        sharedpreferences = getSharedPreferences(Constants.prefName, Context.MODE_PRIVATE);
        setUpRecyclerView();

        mPresenter.loadSensors();
    }

    private void setUpRecyclerView() {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new SensorAdapter(sensorList, SensorActivity.this);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void showSensors(List<Sensor> sensors) {
        if (sensors != null) {
            if (sensors.size() > 0) {
                //todo add the tvNoMeetings text in the layout
//                tvNoMeetings.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
                mAdapter.addItems(sensors);
            } else {
                mRecyclerView.setVisibility(View.GONE);
                //todo to be added later
//                tvNoMeetings.setVisibility(View.VISIBLE);
//                tvNoMeetings.setText(R.string.no_meeting);
            }
        }
    }

    @Override
    public void openLoginActivity() {
        startActivity(LoginActivity.getStartIntent(SensorActivity.this));
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_logout) {

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

        } else if (item.getItemId() == R.id.menu_settings)
            CommonUtils.toast("settings clicked");

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void hideKeyboard() {
        //todo add hideKeyboard functionality in here
    }

    @Override
    public void openRegisterSensorActivity() {
        startActivity(RegisterSensorActivity.getStartIntent(SensorActivity.this));
    }

}
