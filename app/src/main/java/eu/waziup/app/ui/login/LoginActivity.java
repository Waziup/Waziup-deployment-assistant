package eu.waziup.app.ui.login;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;

import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;
import net.openid.appauth.ClientSecretBasic;
import net.openid.appauth.RegistrationRequest;
import net.openid.appauth.ResponseTypeValues;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import eu.waziup.app.DaApp;
import eu.waziup.app.R;
import eu.waziup.app.data.network.model.login.IdentityProvider;
import eu.waziup.app.ui.base.BaseActivity;
import eu.waziup.app.ui.main.MainActivity;
import eu.waziup.app.utils.ConnectivityUtil;
import timber.log.Timber;

public class LoginActivity extends BaseActivity implements LoginMvpView {

    public static final String TAG = "LoginActivity";

    @Inject
    LoginMvpPresenter<LoginMvpView> mPresenter;

    @Inject
    AuthorizationService mAuthService;

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

        checkInternetBeforeProceeding();
    }

    public void checkInternetBeforeProceeding(){
        if (ConnectivityUtil.isConnectedMobile(DaApp.getContext()) || ConnectivityUtil.isConnectedWifi(DaApp.getContext())) {
            startAuth();
        } else {
            showNoInternetAlertDialog();
        }
    }

    public void startAuth() {
        List<IdentityProvider> providers = IdentityProvider.getEnabledProviders(this);
        for (final IdentityProvider idp : providers) {
            final AuthorizationServiceConfiguration.RetrieveConfigurationCallback retrieveCallback =
                    (serviceConfiguration, ex) -> {
                        if (ex != null) {
                            Timber.tag(TAG).w(ex, "Failed to retrieve configuration for %s", idp.name);
                            checkInternetBeforeProceeding();
                        } else {
                            Timber.d("configuration retrieved for %s, proceeding", idp.name);
                            if (serviceConfiguration != null)
                                if (idp.getClientId() == null) {
                                    // Do dynamic client registration if no client_id
                                    makeRegistrationRequest(serviceConfiguration, idp);
                                } else {
                                    makeAuthRequest(serviceConfiguration, idp);
                                }
                        }
                    };

            // calls the retrieveConfig method for retrieving user info from openid
            idp.retrieveConfig(LoginActivity.this, retrieveCallback);
        }
    }

    public void showNoInternetAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.no_internet_turn_on_title)
                .setMessage(R.string.no_internet_turn_on)
                .setPositiveButton(getString(R.string.dialog_retry), (dialog, id) -> {
                    checkInternetBeforeProceeding();
                })
                .setNegativeButton(getString(R.string.exit), (dialog, id) -> {
                    dialog.dismiss();
                    finish();
                });
        AlertDialog alert = builder.create();
        alert.show();
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

        Timber.d("Making auth request to %s", serviceConfig.authorizationEndpoint);
        mAuthService.performAuthorizationRequest(
                authRequest,
                MainActivity.createPostAuthorizationIntent(
                        this,
                        authRequest,
                        serviceConfig.discoveryDoc,
                        idp.getClientSecret()),
                mAuthService.createCustomTabsIntentBuilder()
                        .setToolbarColor(getColorCompat(R.color.chromeTab))
                        .build());
    }

    private void makeRegistrationRequest(
            @NonNull AuthorizationServiceConfiguration serviceConfig,
            @NonNull final IdentityProvider idp) {

        final RegistrationRequest registrationRequest = new RegistrationRequest.Builder(
                serviceConfig,
                Collections.singletonList(idp.getRedirectUri()))
                .setTokenEndpointAuthenticationMethod(ClientSecretBasic.NAME)
                .build();

        Timber.d("Making registration request to %s", serviceConfig.registrationEndpoint);
        mAuthService.performRegistrationRequest(
                registrationRequest,
                (registrationResponse, ex) -> {
                    Timber.d("Registration request complete");
                    if (registrationResponse != null) {
                        idp.setClientId(registrationResponse.clientId);
                        idp.setClientSecret(registrationResponse.clientSecret);
                        Timber.d("Registration request complete successfully");
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
    protected void onDestroy() {
        super.onDestroy();
        if (mAuthService != null)
            mAuthService.dispose();

        if (mPresenter != null)
            mPresenter.onDetach();

    }

    @Override
    protected void setUp() {

    }

}
