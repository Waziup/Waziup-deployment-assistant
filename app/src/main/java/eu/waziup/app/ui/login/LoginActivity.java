package eu.waziup.app.ui.login;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.customtabs.CustomTabsIntent;
import android.util.Log;
import android.widget.Toast;

import net.openid.appauth.AppAuthConfiguration;
import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;
import net.openid.appauth.ClientSecretBasic;
import net.openid.appauth.RegistrationRequest;
import net.openid.appauth.ResponseTypeValues;
import net.openid.appauth.browser.AnyBrowserMatcher;
import net.openid.appauth.browser.BrowserMatcher;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.OnClick;
import eu.waziup.app.R;
import eu.waziup.app.data.network.model.login.IdentityProvider;
import eu.waziup.app.ui.base.BaseActivity;
import eu.waziup.app.ui.main.MainActivity;
import eu.waziup.app.utils.AuthStateManager;
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

        // this is where the layout display is going to happen
        setContentView(R.layout.activity_login);

        getActivityComponent().inject(this);
        setUnBinder(ButterKnife.bind(this));

        mPresenter.onAttach(LoginActivity.this);

        if (mAuthService == null){
            mAuthService = createAuthorizationService();
        }

        setUp();

        if (mAuthStateManager != null
                && mAuthStateManager.getCurrent() != null
                && mAuthStateManager.getCurrent().getAuthorizationServiceConfiguration() != null
                && IdentityProvider.getEnabledProviders(this).size() > 0)
            makeAuthRequest(mAuthStateManager.getCurrent().getAuthorizationServiceConfiguration(), IdentityProvider.getEnabledProviders(this).get(0));

    }

    private AuthorizationService createAuthorizationService() {
        Log.e(TAG, "Creating authorization service");
        AppAuthConfiguration.Builder builder = new AppAuthConfiguration.Builder();
        builder.setBrowserMatcher(mBrowserMatcher);
        builder.setConnectionBuilder(mConfiguration.getConnectionBuilder());

        return new AuthorizationService(this, builder.build());
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

    private void makeAuthRequest(
            @NonNull AuthorizationServiceConfiguration serviceConfig,
            @NonNull IdentityProvider idp) {

        AuthorizationRequest authRequest = new AuthorizationRequest.Builder(
                serviceConfig,
                idp.getClientId(),
                ResponseTypeValues.CODE,
                idp.getRedirectUri())
                .setScope(idp.getScope())
                .build();

        Log.e(TAG, "Making auth request to " + serviceConfig.authorizationEndpoint);
        mAuthService.performAuthorizationRequest(
                authRequest,
                MainActivity.createPostAuthorizationIntent(
                        this,
                        authRequest,
                        serviceConfig.discoveryDoc,
                        idp.getClientSecret()),
                mAuthService.createCustomTabsIntentBuilder()
                        .setToolbarColor(getColorCompat(R.color.colorAccent))
                        .build());
    }

    private void makeRegistrationRequest(
            @NonNull AuthorizationServiceConfiguration serviceConfig,
            @NonNull final IdentityProvider idp) {

        final RegistrationRequest registrationRequest = new RegistrationRequest.Builder(
                serviceConfig,
                Arrays.asList(idp.getRedirectUri()))
                .setTokenEndpointAuthenticationMethod(ClientSecretBasic.NAME)
                .build();

        Log.d(TAG, "Making registration request to " + serviceConfig.registrationEndpoint);
        mAuthService.performRegistrationRequest(
                registrationRequest,
                (registrationResponse, ex) -> {
                    Log.d(TAG, "Registration request complete");
                    if (registrationResponse != null) {
                        idp.setClientId(registrationResponse.clientId);
                        idp.setClientSecret(registrationResponse.clientSecret);
                        Log.d(TAG, "Registration request complete successfully");
                        // Continue with the authentication
                        makeAuthRequest(registrationResponse.request.configuration, idp);
                    }
                });
    }


    @Override
    public void setUp() {

    }


    @OnClick(R.id.start_auth)
    void startAuth() {

    }

    private void warmUpBrowser() {
        Log.e(TAG, "Warming up browser instance for auth request");
        Log.e(TAG, "mAuthRequest " + mAuthRequest.get().toUri());
        CustomTabsIntent.Builder intentBuilder = mAuthService.createCustomTabsIntentBuilder(mAuthRequest.get().toUri());
        intentBuilder.setToolbarColor(getColorCompat(R.color.chromeTab));
        mAuthIntent.set(intentBuilder.build());
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
