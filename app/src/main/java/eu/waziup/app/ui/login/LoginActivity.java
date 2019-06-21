package eu.waziup.app.ui.login;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;
import net.openid.appauth.ClientSecretBasic;
import net.openid.appauth.RegistrationRequest;
import net.openid.appauth.ResponseTypeValues;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.OnClick;
import eu.waziup.app.R;
import eu.waziup.app.data.network.model.login.IdentityProvider;
import eu.waziup.app.ui.base.BaseActivity;
import eu.waziup.app.ui.main.MainActivity;

public class LoginActivity extends BaseActivity implements LoginMvpView {

    public static final String TAG = "LoginActivity";
    @Inject
    LoginMvpPresenter<LoginMvpView> mPresenter;
    private AuthorizationService mAuthService;

    public static Intent getStartIntent(Context context) {
        return new Intent(context, LoginActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // this is where the layout display is going to happen
        setContentView(R.layout.activity_login);

        getActivityComponent().inject(this);
        setUnBinder(ButterKnife.bind(this));

        mPresenter.onAttach(LoginActivity.this);

        mAuthService = new AuthorizationService(this);
        List<IdentityProvider> providers = IdentityProvider.getEnabledProviders(this);

        setUp();

        for (final IdentityProvider idp : providers) {
            final AuthorizationServiceConfiguration.RetrieveConfigurationCallback retrieveCallback =
                    (serviceConfiguration, ex) -> {
                        if (ex != null) {
                            Log.w(TAG, "Failed to retrieve configuration for " + idp.name, ex);
                        } else {
                            Log.d(TAG, "configuration retrieved for " + idp.name
                                    + ", proceeding");
                            if (idp.getClientId() == null) {
                                // Do dynamic client registration if no client_id
                                makeRegistrationRequest(serviceConfiguration, idp);
                            } else {
                                makeAuthRequest(serviceConfiguration, idp);
                            }
                        }
                    };

            FrameLayout idpButton = new FrameLayout(this);
            idpButton.setBackgroundResource(idp.buttonImageRes);
            idpButton.setContentDescription(
                    getResources().getString(idp.buttonContentDescriptionRes));
            idpButton.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            idpButton.setOnClickListener(view -> {
                Log.d(TAG, "initiating auth for " + idp.name);
                idp.retrieveConfig(LoginActivity.this, retrieveCallback);
            });

            TextView label = new TextView(this);
            label.setText(idp.name);
            label.setTextColor(getColorCompat(idp.buttonTextColorRes));
            label.setLayoutParams(new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    Gravity.CENTER));
            idpButton.addView(label);

//            idpButtonContainer.addView(idpButton);
        }

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

        Log.d(TAG, "Making auth request to " + serviceConfig.authorizationEndpoint);
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
                (AuthorizationService.RegistrationResponseCallback) (registrationResponse, ex) -> {
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
        if (mAuthService != null)
            mAuthService.dispose();

        if (mPresenter != null)
            mPresenter.onDetach();

    }

    @Override
    public void setUp() {

    }

    @OnClick(R.id.start_auth)
    void startAuth() {

    }

    @Override
    public void openSensorActivity() {
        hideLoading();
        startActivity(MainActivity.getStartIntent(LoginActivity.this));
        finish();
    }
}
