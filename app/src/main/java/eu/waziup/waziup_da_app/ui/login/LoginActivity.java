package eu.waziup.waziup_da_app.ui.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import eu.waziup.waziup_da_app.R;
import eu.waziup.waziup_da_app.ui.base.BaseActivity;
import eu.waziup.waziup_da_app.ui.sensor.SensorActivity;

public class LoginActivity extends BaseActivity implements LoginMvpView {

    @Inject
    LoginMvpPresenter<LoginMvpView> mPresenter;

    @BindView(R.id.et_username)
    EditText etUsername;

    @BindView(R.id.et_password)
    EditText etPassword;

    public static Intent getStartIntent(Context context) {
        return new Intent(context, LoginActivity.class);
    }

    String username, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActivityComponent().inject(this);
        setUnBinder(ButterKnife.bind(this));

        mPresenter.onAttach(LoginActivity.this);
        mPresenter.onDecideNextActivity();

        setContentView(R.layout.activity_login);

        setUp();

        username = etUsername.getText().toString().trim();
        password = etPassword.getText().toString().trim();

        Log.e("--->username", username);
        Log.e("--->password", password);

    }

    @Override
    public void setUp() {

    }

    @OnClick(R.id.btn_login)
    void onLoginClicked() {
        mPresenter.onServerLoginClick(etUsername.getText().toString().trim(),
                etPassword.getText().toString().trim());
    }

    @Override
    public void hideKeyboard() {

    }

    @Override
    public void openSensorActivity() {
        hideLoading();
        startActivity(SensorActivity.getStartIntent(LoginActivity.this));
        finish();
    }
}
