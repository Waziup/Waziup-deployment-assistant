package eu.waziup.waziup_da_app.ui.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import javax.inject.Inject;

import butterknife.ButterKnife;
import eu.waziup.waziup_da_app.R;
import eu.waziup.waziup_da_app.network.UserClient;
import eu.waziup.waziup_da_app.data.network.model.User;
import eu.waziup.waziup_da_app.ui.base.BaseActivity;
import eu.waziup.waziup_da_app.ui.sensor.SensorActivity;
import eu.waziup.waziup_da_app.utils.CommonUtils;
import eu.waziup.waziup_da_app.utils.Constants;
import eu.waziup.waziup_da_app.utils.RetrofitServiceGenerator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
        if (!storedToken.equals("nothing")) {//!TextUtils.isEmpty(storedToken) ||
            startActivity(new Intent(LoginActivity.this, SensorActivity.class));
            return;
        }

        setContentView(R.layout.activity_login);

        getActivityComponent().inject(this);

        setUnBinder(ButterKnife.bind(this));

        // for hiding the keyboard
//        CommonUtils.hideKeyBoard(this);

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

    public void onLoginClicked() {

        if (TextUtils.isEmpty(etUsername.getText())) {
            etUsername.requestFocus();
            etUsername.setError("Please insert a username.");
            return;
        }

        if (TextUtils.isEmpty(etPassword.getText())) {
            etPassword.requestFocus();
            etPassword.setError("Please insert a password.");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        UserClient userClient = RetrofitServiceGenerator.createService(UserClient.class);

        userClient.login(new User(etUsername.getText().toString().trim(), etPassword.getText().toString().trim()))
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                        if (response.isSuccessful()) {
                            //todo save the user token and proceed
                            if (response.body() != null) {
                                // open new activity
                                progressBar.setVisibility(View.GONE);
                                sharedpreferences.edit().putString(Constants.token, response.body()).apply();
                                startActivity(new Intent(LoginActivity.this, SensorActivity.class));
                            }
                        } else {
                            progressBar.setVisibility(View.GONE);
                            CommonUtils.toast("Invalid credential. Please try again.");
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        CommonUtils.toast("connection failed");
                    }
                });
    }

    @Override
    public void hideKeyboard() {

    }

    @Override
    public void openMainActivity() {

    }

    @Override
    public void openForgetPasswordActivity() {

    }

    @Override
    public boolean getDeviceToken() {
        return false;
    }
}
