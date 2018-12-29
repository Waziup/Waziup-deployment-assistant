package eu.waziup.app.ui.login;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
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
import org.json.JSONObject;

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

        enablePostAuthorizationFlows();
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkIntent(getIntent());
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

        AuthorizationRequest request = builder.build();
        Intent postAuthorizationIntent = new Intent(APP_ID);
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
            new MakeApiCallListener(mAuthState, new AuthorizationService(this));
//            if (mMakeApiCall.getVisibility() == View.GONE) {
//                mMakeApiCall.setVisibility(View.VISIBLE);
//                mMakeApiCall.setOnClickListener(new MakeApiCallListener(this, mAuthState, new AuthorizationService(this)));
//            }
//            if (mSignOut.getVisibility() == View.GONE) {
//                mSignOut.setVisibility(View.VISIBLE);
//                mSignOut.setOnClickListener(new SignOutListener(this));
//            }
        } else {
//            mMakeApiCall.setVisibility(View.GONE);
//            mSignOut.setVisibility(View.GONE);
        }
    }

    private void checkIntent(@Nullable Intent intent) {
        if (intent != null) {
            String action = intent.getAction();
            if (action == null) return;
            switch (action) {
                case APP_ID:
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

    //    THIS IS THE METHOD WHICH IS USED FOR FETCHING THE USER INFORMATION FROM THE AUTHENTICATION SERVER
    public static class MakeApiCallListener implements Button.OnClickListener {
        //        private final MainActivity mMainActivity;
        private AuthState mAuthState;
        private AuthorizationService mAuthorizationService;

        public MakeApiCallListener(@NonNull AuthState authState, @NonNull AuthorizationService authorizationService) {
//            mMainActivity = mainActivity;
            mAuthState = authState;
            mAuthorizationService = authorizationService;
        }

        @Override
        public void onClick(View view) {
            mAuthState.performActionWithFreshTokens(mAuthorizationService, new AuthState.AuthStateAction() {
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
                                String jsonBody = response.body().string();
                                Log.i(LOG_TAG, String.format("User Info Response %s", jsonBody));
                                return new JSONObject(jsonBody);
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
