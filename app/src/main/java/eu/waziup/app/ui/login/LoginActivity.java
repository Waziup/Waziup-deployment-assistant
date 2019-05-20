package eu.waziup.app.ui.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import eu.waziup.app.R;
import eu.waziup.app.ui.base.BaseActivity;
import eu.waziup.app.ui.main.MainActivity;

public class LoginActivity extends BaseActivity implements LoginMvpView {

    public static final String TAG = "LoginActivity";
    private final static int RC_SIGN_IN = 123;

    @Inject
    LoginMvpPresenter<LoginMvpView> mPresenter;

    @BindView(R.id.et_username)
    EditText etUsername;

    @BindView(R.id.et_password)
    EditText etPassword;

    public static Intent getStartIntent(Context context) {
        return new Intent(context, LoginActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActivityComponent().inject(this);


        //check the current user
//        if (mAuth.getCurrentUser() != null)
        openSensorActivity();

        setContentView(R.layout.activity_login);
        setUnBinder(ButterKnife.bind(this));

        mPresenter.onAttach(LoginActivity.this);

        setUp();

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mPresenter.onDetach();
        super.onDestroy();
    }


    @Override
    public void setUp() {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    @OnClick(R.id.btn_login)
    void onLoginClicked() {
        mPresenter.onServerLoginClick(
                TextUtils.isEmpty(etUsername.getText()) ? "" : etUsername.getText().toString().trim(),
                TextUtils.isEmpty(etPassword.getText()) ? "" : etPassword.getText().toString().trim());
    }

    @Override
    public void openSensorActivity() {
        hideLoading();
        startActivity(MainActivity.getStartIntent(LoginActivity.this));
        finish();
    }
}
