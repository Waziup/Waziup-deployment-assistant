package eu.waziup.app.ui.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

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
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListner;
    GoogleSignInClient mGoogleSignInClient;

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
        mAuth = FirebaseAuth.getInstance();

        getActivityComponent().inject(this);


        //check the current user
        if (mAuth.getCurrentUser() != null)
            openSensorActivity();

        setContentView(R.layout.activity_login);
        setUnBinder(ButterKnife.bind(this));

        mPresenter.onAttach(LoginActivity.this);

        setUp();

    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
//        updateUI(currentUser);// TODO HAVE TO GET THE UPDATEUI() METHOD
    }

//    -->> SIGN UP NEW USERS
//    mAuth.createUserWithEmailAndPassword(email, password)
//            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//        @Override
//        public void onComplete(@NonNull Task<AuthResult> task) {
//            if (task.isSuccessful()) {
//                // Sign in success, update UI with the signed-in user's information
//                Log.d(TAG, "createUserWithEmail:success");
//                FirebaseUser user = mAuth.getCurrentUser();
//                updateUI(user);
//            } else {
//                // If sign in fails, display a message to the user.
//                Log.w(TAG, "createUserWithEmail:failure", task.getException());
//                Toast.makeText(EmailPasswordActivity.this, "Authentication failed.",
//                        Toast.LENGTH_SHORT).show(
//                updateUI(null);
//            }
//
//            // ...
//        }
//    });


//    -->> SIGN IN EXISTING USERS
//    mAuth.signInWithEmailAndPassword(email, password)
//            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//        @Override
//        public void onComplete(@NonNull Task<AuthResult> task) {
//            if (task.isSuccessful()) {
//                // Sign in success, update UI with the signed-in user's information
//                Log.d(TAG, "signInWithEmail:success");
//                FirebaseUser user = mAuth.getCurrentUser();
//                updateUI(user);
//            } else {
//                // If sign in fails, display a message to the user.
//                Log.w(TAG, "signInWithEmail:failure", task.getException());
//                Toast.makeText(EmailPasswordActivity.this, "Authentication failed.",
//                        Toast.LENGTH_SHORT).show();
//                updateUI(null);
//            }
//
//            // ...
//        }
//    });

//    -->> ACCESS USER INFORMATION
//    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//    if (user != null) {
//        // Name, email address, and profile photo Url
//        String name = user.getDisplayName();
//        String email = user.getEmail();
//        Uri photoUrl = user.getPhotoUrl();
//
//        // Check if user's email is verified
//        boolean emailVerified = user.isEmailVerified();
//
//        // The user's ID, unique to the Firebase project. Do NOT use this value to
//        // authenticate with your backend server, if you have one. Use
//        // FirebaseUser.getIdToken() instead.
//        String uid = user.getUid();
//    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @OnClick(R.id.btn_google_login)
    void onGoogleClicked() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onDestroy() {
        mPresenter.onDetach();
        super.onDestroy();
    }


    @Override
    public void setUp() {
        mAuth = FirebaseAuth.getInstance();

        mAuthListner = firebaseAuth -> {
            if (firebaseAuth.getCurrentUser() != null)
                openSensorActivity();
        };

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null)
                    firebaseAuthWithGoogle(account);
                else Log.e(TAG, "GoogleSignInAccount account is NULL");
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        //
                        if (user != null) {// the user has already logged in
                            if (user.getDisplayName() != null && user.getEmail() != null && user.getPhotoUrl() != null) {
                                mPresenter.onSaveUserInfo(user.getDisplayName(), user.getEmail(), user.getPhotoUrl().toString());
                                Log.e("--->userInfo", user.getDisplayName());
                            }
                            openSensorActivity();
                        }
//                        mPresenter.updateUserInfo(user.getDisplayName(), );
                        //updateUI(user);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText(this, "Aut Fail", Toast.LENGTH_SHORT).show();
                        //updateUI(null);
                    }
                    // ...
                });
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
