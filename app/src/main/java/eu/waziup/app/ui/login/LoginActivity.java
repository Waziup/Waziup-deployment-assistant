package eu.waziup.app.ui.login;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.customtabs.CustomTabsIntent;
import android.util.Log;

import net.openid.appauth.AppAuthConfiguration;
import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;
import net.openid.appauth.ResponseTypeValues;
import net.openid.appauth.browser.AnyBrowserMatcher;
import net.openid.appauth.browser.BrowserMatcher;

import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.OnClick;
import eu.waziup.app.R;
import eu.waziup.app.ui.base.BaseActivity;
import eu.waziup.app.ui.main.MainActivity;
import eu.waziup.app.utils.AuthStateManager;
import eu.waziup.app.utils.CommonUtils;
import eu.waziup.app.utils.Configuration;

public class LoginActivity extends BaseActivity implements LoginMvpView {

    public static final String TAG = "LoginActivity";
    private static final String EXTRA_FAILED = "failed";
    private static final int RC_AUTH = 100;
    private final AtomicReference<String> mClientId = new AtomicReference<>();
    private final AtomicReference<AuthorizationRequest> mAuthRequest = new AtomicReference<>();
    private final AtomicReference<CustomTabsIntent> mAuthIntent = new AtomicReference<>();


    @Inject
    LoginMvpPresenter<LoginMvpView> mPresenter;

    private AuthorizationService mAuthService;
    private AuthStateManager mAuthStateManager;
    private Configuration mConfiguration;

    @NonNull
    private BrowserMatcher mBrowserMatcher = AnyBrowserMatcher.INSTANCE;

    public static Intent getStartIntent(Context context) {
        return new Intent(context, LoginActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuthStateManager = AuthStateManager.getInstance(this);
        mConfiguration = Configuration.getInstance(this);

        if (mAuthStateManager.getCurrent().isAuthorized()) {// && !mConfiguration.hasConfigurationChanged()
            Log.e(TAG, "Authorized user");

            startActivity(MainActivity.getStartIntent(LoginActivity.this));
            finish();
            return;
        }

        configureBrowserSelector();

        initializeAppAuth();

        // this is where the layout display is going to happen
        setContentView(R.layout.activity_login);

        getActivityComponent().inject(this);
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
        super.onDestroy();

        if (mAuthService != null) {
            mAuthService.dispose();
        }

        if (mPresenter != null)
            mPresenter.onDetach();

    }

    @Override
    public void setUp() {

    }


    @OnClick(R.id.start_auth)
    void startAuth() {
        doAuth();
    }

    private void configureBrowserSelector() {
        mBrowserMatcher = AnyBrowserMatcher.INSTANCE;
        Log.e(TAG, "mBrowserMatcher: " + mBrowserMatcher.toString());
    }

    private void initializeAuthRequest() {
        createAuthRequest();
        warmUpBrowser();
    }

    private void createAuthRequest() {
        if (mAuthStateManager.getCurrent().getAuthorizationServiceConfiguration() != null) {
            AuthorizationRequest.Builder authRequestBuilder = new AuthorizationRequest.Builder(
                    mAuthStateManager.getCurrent().getAuthorizationServiceConfiguration(),
                    mClientId.get(),
                    ResponseTypeValues.CODE,
                    mConfiguration.getRedirectUri())
                    .setScope(mConfiguration.getScope());

            mAuthRequest.set(authRequestBuilder.build());
        }
    }

    private void warmUpBrowser() {
        Log.e(TAG, "Warming up browser instance for auth request");
        Log.e(TAG, "mAuthRequest " + mAuthRequest.get().toUri());
        CustomTabsIntent.Builder intentBuilder = mAuthService.createCustomTabsIntentBuilder(mAuthRequest.get().toUri());
        intentBuilder.setToolbarColor(getColorCompat(R.color.chromeTab));
        mAuthIntent.set(intentBuilder.build());
    }

    private void initializeAppAuth() {
        recreateAuthorizationService();

        // configuration is already created, skip to client initialization
        Log.e(TAG, "auth config already established");

        Log.i(TAG, "Creating auth config from res/raw/auth_config.json");
        if (mConfiguration.getAuthEndpointUri() != null && mConfiguration.getTokenEndpointUri() != null) {
            AuthorizationServiceConfiguration config = new AuthorizationServiceConfiguration(
                    mConfiguration.getAuthEndpointUri(),
                    mConfiguration.getTokenEndpointUri(),
                    mConfiguration.getRegistrationEndpointUri());

            mAuthStateManager.replace(new AuthState(config));
            initializeClient();

//            -----------------------------------------------------------------------------------
            Log.e(TAG, "====> doAuth");
            // do Authentication
            doAuth();
            Log.e(TAG, "=====>doAuthComplete");
        }
    }

    private void doAuth() {
        // performAuthorizationRequest -> opening the chromeCustomTab
        Intent completionIntent = new Intent(this, MainActivity.class);
        Intent cancelIntent = new Intent(this, LoginActivity.class);
        cancelIntent.putExtra(EXTRA_FAILED, true);
        cancelIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        Log.e(TAG, "====> doAuth  =====>");
        mAuthService.performAuthorizationRequest(
                mAuthRequest.get(),
                PendingIntent.getActivity(this, 0, completionIntent, 0),
                PendingIntent.getActivity(this, 0, cancelIntent, 0),
                mAuthIntent.get());

//        AuthorizationService authService = new AuthorizationService(this);
//        Intent authIntent = authService.getAuthorizationRequestIntent(mAuthRequest.get(), mAuthIntent.get());
//        startActivityForResult(authIntent, RC_AUTH);

    }

    private void initializeClient() {
        if (mConfiguration.getClientId() != null) {
            Log.i(TAG, "Using static client ID: " + mConfiguration.getClientId());
            // use a statically configured client ID
            mClientId.set(mConfiguration.getClientId());
            runOnUiThread(this::initializeAuthRequest);
            return;
        }
    }

    private void recreateAuthorizationService() {
        if (mAuthService != null) {
            Log.e(TAG, "Discarding existing AuthService instance");
            mAuthService.dispose();
        }
        mAuthService = createAuthorizationService();
        mAuthRequest.set(null);
        mAuthIntent.set(null);
    }

    private AuthorizationService createAuthorizationService() {
        Log.e(TAG, "Creating authorization service");
        AppAuthConfiguration.Builder builder = new AppAuthConfiguration.Builder();
        builder.setBrowserMatcher(mBrowserMatcher);
        builder.setConnectionBuilder(mConfiguration.getConnectionBuilder());

        return new AuthorizationService(this, builder.build());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.e(TAG, "--->onActivityResult");
        if (resultCode == RESULT_CANCELED) {
            // do something here
            Log.e(TAG, "onActivityResult---->RESULT_CANCELED");
            CommonUtils.toast("RESULT_CANCELED");
        } else if (requestCode == RC_AUTH) {
            // updated logged out mode and
            AuthorizationResponse resp = AuthorizationResponse.fromIntent(data);
            AuthorizationException ex = AuthorizationException.fromIntent(data);
            final AuthState authState = new AuthState(resp, ex);
            if (resp != null) {
//                Log.i(TAG, String.format("Handled Authorization Response %s ", authState.toJsonString()));
                AuthorizationService service = new AuthorizationService(this);
                service.performTokenRequest(resp.createTokenExchangeRequest(), (tokenResponse, exception) -> {
                    if (exception != null) {
                        Log.e(TAG, "Token Exchange failed", exception);
                    } else {
                        if (tokenResponse != null) {
                            authState.update(tokenResponse, exception);
                            mAuthStateManager.updateAfterTokenResponse(tokenResponse, exception);
//                            persistAuthState(authState);
                            Log.e(TAG, String.format("Token Response [ Access Token: %s, ID Token: %s ]", tokenResponse.accessToken, tokenResponse.idToken));
                            startActivity(MainActivity.getStartIntent(LoginActivity.this).putExtras(data.getExtras()));
                        }
                    }
                });
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @SuppressWarnings("deprecation")
    private int getColorCompat(@ColorRes int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return getColor(color);
        } else {
            return getResources().getColor(color);
        }
    }

    @Override
    public void openSensorActivity() {
        hideLoading();
        startActivity(MainActivity.getStartIntent(LoginActivity.this));
        finish();
    }
}
