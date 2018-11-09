package eu.waziup.waziup_da_app.ui.detail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import javax.inject.Inject;

import eu.waziup.waziup_da_app.R;
import eu.waziup.waziup_da_app.ui.base.BaseActivity;
import eu.waziup.waziup_da_app.ui.login.LoginActivity;
import eu.waziup.waziup_da_app.ui.login.LoginMvpPresenter;
import eu.waziup.waziup_da_app.ui.login.LoginMvpView;

public class DetailSensorActivity extends BaseActivity implements DetailSensorMvpView {

    @Inject
    DetailSensorMvpPresenter<DetailSensorMvpView> mPresenter;

    public static Intent getStartIntent(Context context) {
        return new Intent(context, DetailSensorActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_sensor);
    }

    @Override
    protected void setUp() {

    }

    @Override
    public void hideKeyboard() {

    }
}
