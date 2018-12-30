package eu.waziup.app.ui.login;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.RestrictionsManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.UserManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import eu.waziup.app.DaApp;
import eu.waziup.app.R;
import eu.waziup.app.ui.base.BaseActivity;
import eu.waziup.app.ui.main.MainActivity;
import eu.waziup.app.utils.CommonUtils;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static eu.waziup.app.DaApp.LOG_TAG;
import static eu.waziup.app.utils.AppConstants.APP_ID;
import static eu.waziup.app.utils.AppConstants.AUTH_CLIENT_ID;
import static eu.waziup.app.utils.AppConstants.AUTH_ENDPOINT;
import static eu.waziup.app.utils.AppConstants.AUTH_REDIRECT_URI;
import static eu.waziup.app.utils.AppConstants.TOKEN_ENDPOINT;

public class LoginActivity extends BaseActivity implements LoginMvpView {

    private static final String SHARED_PREFERENCES_NAME = "AuthStatePreference";
    private static final String AUTH_STATE = "AUTH_STATE";
    private static final String USED_INTENT = "USED_INTENT";
    private static final String LOGIN_HINT = "login_hint";

    // state
    AuthState mAuthState;

    // login hint;
    String mLoginHint;

    // broadcast receiver for app restrictions changed broadcast
    private BroadcastReceiver mRestrictionsReceiver;

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

        enablePostAuthorizationFlows();

        // Retrieve app restrictions and take appropriate action
        getAppRestrictions();
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkIntent(getIntent());

        // Register a receiver for app restrictions changed broadcast
        registerRestrictionsReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Retrieve app restrictions and take appropriate action
        getAppRestrictions();

        // Register a receiver for app restrictions changed broadcast
        registerRestrictionsReceiver();
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Unregister receiver for app restrictions changed broadcast
        unregisterReceiver(mRestrictionsReceiver);
    }

    @OnClick(R.id.btn_google_login)
    void onGoogleClicked() {
        AuthorizationServiceConfiguration serviceConfiguration = new AuthorizationServiceConfiguration(
                Uri.parse(AUTH_ENDPOINT) /* auth endpoint */,
                Uri.parse(TOKEN_ENDPOINT) /* token endpoint */
        );
        AuthorizationService authorizationService = new AuthorizationService(this);
        Uri redirectUri = Uri.parse(AUTH_REDIRECT_URI);//Uri.parse("com.google.codelabs.appauth:/oauth2callback");
        AuthorizationRequest.Builder builder = new AuthorizationRequest.Builder(
                serviceConfiguration,
                AUTH_CLIENT_ID,
                AuthorizationRequest.RESPONSE_TYPE_CODE,
                redirectUri
        );

        builder.setScopes("profile");// other available scopes are: email, username

        if (getLoginHint() != null) {
            Map<String, String> loginHintMap = new HashMap<>();
            loginHintMap.put(LOGIN_HINT, getLoginHint());
            builder.setAdditionalParameters(loginHintMap);

            Log.i(LOG_TAG, String.format("login_hint: %s", getLoginHint()));
        }

        AuthorizationRequest request = builder.build();
        Intent postAuthorizationIntent = new Intent(APP_ID + ".HANDLE_AUTHORIZATION_RESPONSE");
        PendingIntent pendingIntent = PendingIntent.getActivity(this, request.hashCode(), postAuthorizationIntent, 0);
        authorizationService.performAuthorizationRequest(request, pendingIntent);
    }

    private void persistAuthState(@NonNull AuthState authState) {
        getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).edit()
                .putString(AUTH_STATE, authState.toJsonString())
                .apply();
        enablePostAuthorizationFlows();
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
            makeApiCallListener(mAuthState, new AuthorizationService(this));
//            if (mMakeApiCall.getVisibility() == View.GONE) {
//                mMakeApiCall.setVisibility(View.VISIBLE);
//                mMakeApiCall.setOnClickListener(new MakeApiCallListener(this, mAuthState, new AuthorizationService(this)));
//            }
//            if (mSignOut.getVisibility() == View.GONE) {
//                mSignOut.setVisibility(View.VISIBLE);
//                mSignOut.setOnClickListener(new SignOutListener(this));
//            }
//        } else {
//            mMakeApiCall.setVisibility(View.GONE);
//            mSignOut.setVisibility(View.GONE);
        }
    }

    private void checkIntent(@Nullable Intent intent) {
        if (intent != null) {
            String action = intent.getAction();
            if (action == null) return;
            Log.e(LOG_TAG,"USED_INTENT--"+String.valueOf(USED_INTENT));
            switch (action) {
                case APP_ID + ".HANDLE_AUTHORIZATION_RESPONSE":
                    if (!intent.hasExtra(USED_INTENT)) {
                        handleAuthorizationResponse(intent);
                        intent.putExtra(USED_INTENT, true);
                    }
                    break;
                default:
                    // do nothing
            }
        }
    }

    public void makeApiCallListener(@NonNull AuthState authState, @NonNull AuthorizationService authorizationService) {
        // todo have to handle this or activity leaks
        authState.performActionWithFreshTokens(authorizationService, new AuthState.AuthStateAction() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void execute(@Nullable String accessToken, @Nullable String idToken, @Nullable AuthorizationException exception) {
                new AsyncTask<String, Void, JSONObject>() {
                    @Override
                    protected JSONObject doInBackground(String... tokens) {
                        OkHttpClient client = new OkHttpClient();
                        Request request = new Request.Builder()
                                .url("https://www.googleapis.com/oauth2/v3/userinfo")
                                .addHeader("Authorization", String.format("Bearer %s", tokens[0]))
                                .build();

                        try {
                            Response response = client.newCall(request).execute();
                            assert response.body() != null;
                            String jsonBody = response.body().string();
                            JSONObject userInfo = new JSONObject(jsonBody);
                            Log.i(LOG_TAG, String.format("User Info Response %s", jsonBody));
                            // todo find a better way of handling this later
                            mPresenter.onSaveName(String.valueOf(userInfo.getString("name")));
                            mPresenter.onSavePicture(String.valueOf(userInfo.getString("picture")));
//                            return new JSONObject(jsonBody);
                        } catch (Exception exception) {
                            Log.w(LOG_TAG, exception);
                        }
                        return null;
                    }

                    // todo think of adding this to main activity of the application
                    @Override
                    protected void onPostExecute(JSONObject userInfo) {
                        if (userInfo != null) {
                            String fullName = userInfo.optString("name", null);
                            String givenName = userInfo.optString("given_name", null);
                            String familyName = userInfo.optString("family_name", null);
                            String imageUrl = userInfo.optString("picture", null);
                            if (!TextUtils.isEmpty(imageUrl)) {
                                Log.e(LOG_TAG, "imageUrl" + imageUrl);
//                                    Picasso.get()
//                                            .load(imageUrl)
//                                            .placeholder(R.drawable.ic_account_circle_black_48dp)
//                                            .into(mMainActivity.mProfileView);
                            }
                            if (!TextUtils.isEmpty(fullName)) {
                                Log.e(LOG_TAG, "fullName" + fullName);
//                                    mMainActivity.mFullName.setText(fullName);
                            }
                            if (!TextUtils.isEmpty(givenName)) {
                                Log.e(LOG_TAG, "givenName" + givenName);
//                                    mMainActivity.mGivenName.setText(givenName);
                            }
                            if (!TextUtils.isEmpty(familyName)) {
                                Log.e(LOG_TAG, "familyName" + familyName);
//                                    mMainActivity.mFamilyName.setText(familyName);
                            }

                            String message;
                            if (userInfo.has("error")) {
                                message = String.format("%s [%s]", DaApp.getContext().getString(R.string.request_failed),
                                        userInfo.optString("error_description", "No description"));
                            } else {
                                message = DaApp.getContext().getString(R.string.request_complete);
                            }
                            CommonUtils.toast(message);
                            Log.e(LOG_TAG, "message" + message);
//                                Snackbar.make(, message, Snackbar.LENGTH_SHORT)
//                                        .show();
                        }
                    }
                }.execute(accessToken);
            }
        });
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
                        authState.update(tokenResponse, null);// was exception, not null
                        persistAuthState(authState);
                        Log.i(LOG_TAG, String.format("Token Response [ Access Token: %s, ID Token: %s ]",
                                tokenResponse.accessToken, tokenResponse.idToken));
                    }
                }
            });
        }else{
            Log.i(LOG_TAG, "Handled Authorization Response failed");
        }
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

    public String getLoginHint() {
        return mLoginHint;
    }

    private void getAppRestrictions() {
        RestrictionsManager restrictionsManager =
                (RestrictionsManager) this
                        .getSystemService(Context.RESTRICTIONS_SERVICE);

        assert restrictionsManager != null;
        Bundle appRestrictions = restrictionsManager.getApplicationRestrictions();

        // Block user if KEY_RESTRICTIONS_PENDING is true, and save login hint if available
        if (!appRestrictions.isEmpty()) {
            if (!appRestrictions.getBoolean(UserManager.
                    KEY_RESTRICTIONS_PENDING)) {
                mLoginHint = appRestrictions.getString(LOGIN_HINT);
            } else {
                Toast.makeText(this, R.string.restrictions_pending_block_user,
                        Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    private void registerRestrictionsReceiver() {
        IntentFilter restrictionsFilter =
                new IntentFilter(Intent.ACTION_APPLICATION_RESTRICTIONS_CHANGED);

        mRestrictionsReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                getAppRestrictions();
            }
        };

        registerReceiver(mRestrictionsReceiver, restrictionsFilter);
    }

}
