package eu.waziup.waziup_da_app.ui.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.OnClick;
import eu.waziup.waziup_da_app.R;
import eu.waziup.waziup_da_app.ui.base.BaseActivity;
import eu.waziup.waziup_da_app.ui.sensor.SensorActivity;
import eu.waziup.waziup_da_app.utils.Constants;

public class LoginActivity extends BaseActivity implements LoginMvpView {

    @Inject
    LoginMvpPresenter<LoginMvpView> mPresenter;

    EditText etUsername, etPassword;
    Button btnLogin;
    ProgressBar progressBar;

    SharedPreferences sharedpreferences;
    String username, password;

    public static Intent getStartIntent(Context context) {
        return new Intent(context, LoginActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedpreferences = getSharedPreferences(Constants.prefName, Context.MODE_PRIVATE);

        String storedToken = sharedpreferences.getString(Constants.token, "nothing");
        Log.e("====>LoginToken", storedToken);

        mPresenter.onAttach(LoginActivity.this);
        mPresenter.onDecideNextActivity();

//        if (!storedToken.equals("nothing")) {//!TextUtils.isEmpty(storedToken) ||
//            startActivity(new Intent(LoginActivity.this, SensorActivity.class));
//            return;
//        }

        setContentView(R.layout.activity_login);
        getActivityComponent().inject(this);
        setUnBinder(ButterKnife.bind(this));

        setUp();

        username = etUsername.getText().toString().trim();
        password = etPassword.getText().toString().trim();

        // attaching onClickListener on the login button click
        btnLogin.setOnClickListener(v -> onLoginClicked());
    }


    @Override
    public void setUp() {
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        progressBar = findViewById(R.id.progressBar_cyclic);

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
    }
}
