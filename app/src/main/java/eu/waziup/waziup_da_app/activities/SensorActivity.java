package eu.waziup.waziup_da_app.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import eu.waziup.waziup_da_app.R;
import eu.waziup.waziup_da_app.adapters.SensorAdapter;
import eu.waziup.waziup_da_app.base.BaseActivity;
import eu.waziup.waziup_da_app.clients.SensorClient;
import eu.waziup.waziup_da_app.models.sensor.Sensor;
import eu.waziup.waziup_da_app.utils.CommonUtils;
import eu.waziup.waziup_da_app.utils.Constants;
import eu.waziup.waziup_da_app.utils.RetrofitServiceGenerator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SensorActivity extends BaseActivity {

    RecyclerView mRecyclerView;
    SensorAdapter mAdapter;
    //    Toolbar mToolbar;
    List<Sensor> sensorList = new ArrayList<>();
    SharedPreferences sharedpreferences;
    SwipeRefreshLayout mSwipeRefreshLayout;
    ProgressBar mProgressBar;
    FloatingActionButton mfab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

        setUp();

        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            fetchSensors();
            mSwipeRefreshLayout.setRefreshing(false);
        });

        mfab.setOnClickListener(view -> {
            startActivity(new Intent(SensorActivity.this, RegisterSensorActivity.class));
        });
    }

    @Override
    protected void setUp() {
//        mToolbar = findViewById(R.id.sensor_toolbar);
//        mToolbar.setTitle("Sensors");
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle("Sensors");

        mRecyclerView = findViewById(R.id.sensor_recycler);
        mProgressBar = findViewById(R.id.progressbar_sensor);
        mSwipeRefreshLayout = findViewById(R.id.sensor_swipe_to_refresh);
        mfab = findViewById(R.id.fab_sensor);

        sharedpreferences = getSharedPreferences(Constants.prefName, Context.MODE_PRIVATE);
        setUpRecyclerView();

        fetchSensors();
    }

    private void setUpRecyclerView() {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new SensorAdapter(sensorList, SensorActivity.this);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);
    }

    private void fetchSensors() {

        String token = sharedpreferences.getString(Constants.token, "nothing");
        SensorClient sensorClient = RetrofitServiceGenerator.createService(SensorClient.class, token);

        mProgressBar.setVisibility(View.VISIBLE);
        sensorClient.getSensors().enqueue(new Callback<List<Sensor>>() {
            @Override
            public void onResponse(@NonNull Call<List<Sensor>> call, @NonNull Response<List<Sensor>> response) {
                if (response.isSuccessful()) {
                    mProgressBar.setVisibility(View.GONE);
                    mSwipeRefreshLayout.setRefreshing(false);
                    if (response.body() != null) {
                        showSensors(response.body());
                    }
                } else {
                    mProgressBar.setVisibility(View.GONE);
                    mSwipeRefreshLayout.setRefreshing(false);
                    CommonUtils.toast("fetching unsuccessful");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Sensor>> call, @NonNull Throwable t) {
                mProgressBar.setVisibility(View.GONE);
                mSwipeRefreshLayout.setRefreshing(false);
                CommonUtils.toast("connection failed");
            }
        });
    }

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

        mProgressBar.setVisibility(View.GONE);
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
                        // removing the token when the user logout
                        sharedpreferences.edit().putString(Constants.token, "nothing").apply();
                        startActivity(new Intent(SensorActivity.this, LoginActivity.class));
                        finish();
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
}
