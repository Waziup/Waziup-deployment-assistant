package eu.waziup.waziup_da_app.ui.sensor;

import android.content.Context;
import android.content.Intent;
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
import android.widget.TextView;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import eu.waziup.waziup_da_app.R;
import eu.waziup.waziup_da_app.data.network.model.sensor.Sensor;
import eu.waziup.waziup_da_app.ui.base.BaseActivity;
import eu.waziup.waziup_da_app.ui.detail.DetailSensorActivity;
import eu.waziup.waziup_da_app.ui.login.LoginActivity;
import eu.waziup.waziup_da_app.ui.register.RegisterSensorActivity;
import eu.waziup.waziup_da_app.utils.CommonUtils;

public class SensorActivity extends BaseActivity implements SensorMvpView, SensorAdapter.Callback {

    @Inject
    SensorMvpPresenter<SensorMvpView> mPresenter;

    @Inject
    SensorAdapter mAdapter;

    @BindView(R.id.sensor_recycler)
    RecyclerView mRecyclerView;

    @BindView(R.id.sensor_swipe_to_refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.fab_sensor)
    FloatingActionButton mfab;

    @BindView(R.id.tv_no_sensor)
    TextView tvNoSensors;

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
        mAdapter.setCallback(this);

        setUp();

        mPresenter.loadSensors();

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

        setUpRecyclerView();

        mPresenter.loadSensors();
    }

    private void setUpRecyclerView() {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void showSensors(List<Sensor> sensors) {
        if (sensors != null) {
            if (sensors.size() > 0) {
                tvNoSensors.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
                mAdapter.addItems(sensors);
            } else {
                mRecyclerView.setVisibility(View.GONE);
                tvNoSensors.setVisibility(View.VISIBLE);
                tvNoSensors.setText(R.string.no_sensors_list_found);
            }
        }
        hideLoading();
    }

    @Override
    public void openLoginActivity() {
        startActivity(LoginActivity.getStartIntent(SensorActivity.this));
        finish();
    }

    @Override
    public void openDetailSensorActivity(Sensor sensor) {
        //todo pass data like the sensor's id with the intent
        startActivity(DetailSensorActivity.getStartIntent(SensorActivity.this));
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
    public void openRegisterSensorActivity() {
        startActivity(RegisterSensorActivity.getStartIntent(SensorActivity.this));
    }

    @Override
    public void onItemClicked(Sensor sensor) {
        mPresenter.onSensorItemClicked(sensor);
    }
}
