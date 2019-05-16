package eu.waziup.app.ui.login;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;

import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;

import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;

import butterknife.ButterKnife;
import eu.waziup.app.R;
import eu.waziup.app.ui.base.BaseActivity;
import eu.waziup.app.ui.main.MainActivity;

public class LoginActivity extends BaseActivity implements LoginMvpView {

    public static final String TAG = "LoginActivity";

    @Inject
    LoginMvpPresenter<LoginMvpView> mPresenter;
    private final AtomicReference<AuthorizationRequest> mAuthRequest = new AtomicReference<>();

    public static Intent getStartIntent(Context context) {
        return new Intent(context, LoginActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActivityComponent().inject(this);

        setContentView(R.layout.activity_login);
        setUnBinder(ButterKnife.bind(this));

        mPresenter.onAttach(LoginActivity.this);

        setUp();

        AuthorizationServiceConfiguration serviceConfiguration = new AuthorizationServiceConfiguration(
                Uri.parse("https://keycloak.staging.waziup.io/auth/realms/waziup/protocol/openid-connect/auth") /* auth endpoint */,
                Uri.parse("https://keycloak.staging.waziup.io/auth/realms/waziup/protocol/openid-connect/token") /* token endpoint */
        );

        String clientId = "dashboard";
        Uri redirectUri = Uri.parse("net.openid.appauthdemo:/oauth2redirect");
        AuthorizationRequest.Builder builder = new AuthorizationRequest.Builder(
                serviceConfiguration,
                clientId,
                AuthorizationRequest.RESPONSE_TYPE_CODE,
                redirectUri
        );
        builder.setScopes("openid email profile");
        AuthorizationRequest request = builder.build();

        AuthorizationService authorizationService = new AuthorizationService(this);

        String action = "com.google.codelabs.appauth.HANDLE_AUTHORIZATION_RESPONSE";
        Intent postAuthorizationIntent = new Intent(action);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, request.hashCode(), postAuthorizationIntent, 0);
        authorizationService.performAuthorizationRequest(request, pendingIntent);
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
    public void openSensorActivity() {
        hideLoading();
        startActivity(MainActivity.getStartIntent(LoginActivity.this));
        finish();
    }
}
