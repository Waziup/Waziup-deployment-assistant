package eu.waziup.app.ui.login;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.AppCompatButton;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;

import org.json.JSONException;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import eu.waziup.app.R;
import eu.waziup.app.ui.base.BaseActivity;
import eu.waziup.app.ui.main.MainActivity;
import eu.waziup.app.utils.AppConstants;

import static eu.waziup.app.DaApp.LOG_TAG;

public class LoginActivity extends BaseActivity implements LoginMvpView {

    private static final String SHARED_PREFERENCES_NAME = "AuthStatePreference";
    private static final String AUTH_STATE = "AUTH_STATE";
    private static final String USED_INTENT = "USED_INTENT";

    // state
    AuthState mAuthState;

    // views
    AppCompatButton mAuthorize;
    AppCompatButton mMakeApiCall;
    AppCompatButton mSignOut;

    @Inject
    LoginMvpPresenter<LoginMvpView> mPresenter;

    public static Intent getStartIntent(Context context) {
        return new Intent(context, LoginActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActivityComponent().inject(this);

        mPresenter.onAttach(LoginActivity.this);

        setContentView(R.layout.activity_login);
        setUnBinder(ButterKnife.bind(this));
        setUp();
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkIntent(getIntent());
    }

    @OnClick(R.id.btn_google_login)
    void onGoogleClicked() {
        AuthorizationServiceConfiguration serviceConfiguration = new AuthorizationServiceConfiguration(
                Uri.parse("https://accounts.google.com/o/oauth2/v2/auth") /* auth endpoint */,
                Uri.parse("https://www.googleapis.com/oauth2/v4/token") /* token endpoint */
        );

//        Uri redirectUri = Uri.parse("eu.waziup.app:ietf:wg:oauth:2.0:oob");//com.google.codelabs.appauth:/oauth2callback
        Uri redirectUri = Uri.parse(AppConstants.AUTH_REDIRECT_URI);//com.google.codelabs.appauth:/oauth2callback
        AuthorizationRequest.Builder builder = new AuthorizationRequest.Builder(
                serviceConfiguration,
                AppConstants.AUTH_CLIENT_ID,
                AuthorizationRequest.RESPONSE_TYPE_CODE,
                redirectUri
        );
        builder.setScopes("profile");
        AuthorizationRequest request = builder.build();

        AuthorizationService authorizationService = new AuthorizationService(this);

        String action = "eu.waziup.app.HANDLE_AUTHORIZATION_RESPONSE";
        Intent postAuthorizationIntent = new Intent(action);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, request.hashCode(), postAuthorizationIntent, 0);
        authorizationService.performAuthorizationRequest(request, pendingIntent);
    }

    @OnClick(R.id.authorize)
    void onAuthorizeClicked() {
        new AuthorizeListener();
    }

    @BindView(R.id.et_username)
    TextInputEditText etUsername;

    @BindView(R.id.et_password)
    TextInputEditText etPassword;

    @Override
    public void setUp() {

    }

    @Override
    protected void onNewIntent(Intent intent) {
        checkIntent(intent);
    }

    private void enablePostAuthorizationFlows() {
        mAuthState = restoreAuthState();
        if (mAuthState != null && mAuthState.isAuthorized()) {
            if (mMakeApiCall.getVisibility() == View.GONE) {
                mMakeApiCall.setVisibility(View.VISIBLE);
                mMakeApiCall.setOnClickListener(new MakeApiCallListener(this, mAuthState, new AuthorizationService(this)));
            }
            if (mSignOut.getVisibility() == View.GONE) {
                mSignOut.setVisibility(View.VISIBLE);
                mSignOut.setOnClickListener(new SignOutListener(this));
            }
        } else {
            mMakeApiCall.setVisibility(View.GONE);
            mSignOut.setVisibility(View.GONE);
        }
    }

    private void checkIntent(@Nullable Intent intent) {
        if (intent != null) {
            String action = intent.getAction();
            if (action == null) return;
            switch (action) {
                case "eu.waziup.app.HANDLE_AUTHORIZATION_RESPONSE":
                    if (!intent.hasExtra(USED_INTENT)) {
                        handleAuthorizationResponse(intent);
                        intent.putExtra(USED_INTENT, true);
                        // todo remove the below line
                        Toast.makeText(this, USED_INTENT + String.valueOf(true), Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    // do nothing
            }
        }
    }

    public static class MakeApiCallListener implements Button.OnClickListener {

        private final LoginActivity mMainActivity;
        private AuthState mAuthState;
        private AuthorizationService mAuthorizationService;

        public MakeApiCallListener(@NonNull LoginActivity mainActivity, @NonNull AuthState authState, @NonNull AuthorizationService authorizationService) {
            mMainActivity = mainActivity;
            mAuthState = authState;
            mAuthorizationService = authorizationService;
        }

        @Override
        public void onClick(View view) {

            // code from the section 'Making API Calls' goes here

        }
    }

    public static class SignOutListener implements Button.OnClickListener {

        private final LoginActivity mLoginActivity;

        public SignOutListener(@NonNull LoginActivity mainActivity) {
            mLoginActivity = mainActivity;
        }

        @Override
        public void onClick(View view) {
            mLoginActivity.mAuthState = null;
            mLoginActivity.clearAuthState();
            mLoginActivity.enablePostAuthorizationFlows();
        }
    }

    /**
     * Exchanges the code, for the {@link }.//TokenResponse
     *
     * @param intent represents the {@link Intent} from the Custom Tabs or the System Browser.
     */
    private void handleAuthorizationResponse(@NonNull Intent intent) {
        AuthorizationResponse response = AuthorizationResponse.fromIntent(intent);
        AuthorizationException error = AuthorizationException.fromIntent(intent);
        final AuthState authState = new AuthState(response, error);

        if (response != null) {
            Log.i(LOG_TAG, String.format("Handled Authorization Response %s ", authState.toJsonString()));
            AuthorizationService service = new AuthorizationService(this);
            service.performTokenRequest(response.createTokenExchangeRequest(), (tokenResponse, exception) -> {
                if (exception != null) {
                    Log.w(LOG_TAG, "Token Exchange failed", exception);
                } else {
                    if (tokenResponse != null) {
                        authState.update(tokenResponse, exception);
                        persistAuthState(authState);
                        Log.i(LOG_TAG, String.format("Token Response [ Access Token: %s, ID Token: %s ]", tokenResponse.accessToken, tokenResponse.idToken));
                    }
                }
            });
        }
    }

    /**
     * Kicks off the authorization flow.
     */
    public static class AuthorizeListener implements Button.OnClickListener {
        @Override
        public void onClick(View view) {

            // code from the step 'Create the Authorization Request',
            // and the step 'Perform the Authorization Request' goes here.

        }
    }

    private void persistAuthState(@NonNull AuthState authState) {
        getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).edit()
                .putString(AUTH_STATE, authState.toJsonString())
                .apply();// was commit()
        enablePostAuthorizationFlows();
    }

    private void clearAuthState() {
        getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
                .edit()
                .remove(AUTH_STATE)
                .apply();
    }

    @Nullable
    private AuthState restoreAuthState() {
        String jsonString = getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
                .getString(AUTH_STATE, null);
        if (!TextUtils.isEmpty(jsonString)) {
            try {
                return AuthState.fromJson(jsonString);
            } catch (JSONException jsonException) {
                // should never happen
            }
        }
        return null;
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
