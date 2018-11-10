package eu.waziup.waziup_da_app.ui.register;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import javax.inject.Inject;

import butterknife.ButterKnife;
import eu.waziup.waziup_da_app.R;
import eu.waziup.waziup_da_app.ui.base.BaseActivity;

public class RegisterSensorActivity extends BaseActivity implements RegisterSensorMvpView {

    @Inject
    RegisterSensorMvpPresenter<RegisterSensorMvpView> mPresenter;

    public static Intent getStartIntent(Context context) {
        return new Intent(context, RegisterSensorActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_sensor);

        getActivityComponent().inject(this);
        setUnBinder(ButterKnife.bind(this));

        mPresenter.onAttach(RegisterSensorActivity.this);

        hideKeyboard(this);
        setUp();

    }

    @Override
    protected void setUp() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Register Sensor");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void hideKeyboard() {
    }
}
