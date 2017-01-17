package net.livingrecordings.giggermainapp.LoginScreens;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProfileChangeRequest;


import net.livingrecordings.giggermainapp.MainActivity;
import net.livingrecordings.giggermainapp.R;


/**
 * Created by Kraetzig Neu on 22.11.2016.
 */


public class LoginActivity extends Activity
        implements
        View.OnClickListener {


    // mal das alles in die Values übernehmen...
    // für mehrere stati in einem, bitset nehmen java.util.bitset
    public static final int LOGINDLGSTATE_NORMAL = 0;
    public static final int LOGINDLGSTATE_SHOWEMAIL = 1;
    public static final int LOGINDLGSTATE_EMAILREGISTRATION = 2;
    public static final int LOGINDLGSTATE_LOGGEDIN = 3;
    public static final int LOGINDLGSTATE_NORMAL_FORCEIT = 4;

    //firebase..
    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;
    public boolean silentSingIn = false;
    boolean logSuc = false;
    TextView signInWelcome;
    Boolean cameFromLogScreen = false;
    //   ProgressBar progressBar;
    LinearLayout signinwelcome, mainContent, manin_loginscrren, rLayout;
    ProgressDialog progressBarLinLay;
    TextView textUpperHint;
    SignInButton googleBtt;
    View rootView;
    int logState;
    Button regBtt, MailLoginBtt, log_out_btt, backToProfileBtt;
    FirebaseAuth mAuth;
    GoogleApiClient mGoogleApiClient;
    FirebaseAuth.AuthStateListener authStateListener;
    CheckBox cb;
    // wenn der intent erfolgreich it google angemeldet hat,
    // gebe ich den soeben eingeloggten google accout an firebase weiter.
    AuthCredential googleCredential;
    String email;
    String password;
    private int mShortAnimationDuration = 1300;
    // standard callback fürs sign out
    ResultCallback<Status> classicSignOutCallback = new ResultCallback<Status>() {
        @Override
        public void onResult(Status status) {
            FirebaseAuth.getInstance().signOut();
            prepareLogScreen(LOGINDLGSTATE_NORMAL);
            ProgrssBarVisible(false);
        }
    };
    OnCompleteListener<AuthResult> onGoogleSignInComp = new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
            ProgrssBarVisible(false);
            // sucsess???
            if (!task.isSuccessful()) {
                Log.w(TAG, "signInWithCredential", task.getException());
                Toast.makeText(getApplicationContext(), getString(R.string.auth_failed_tech_fb) + " Fehlermeldung: " + task.getException(),
                        Toast.LENGTH_SHORT).show();
            } else {
                // ich kann den Dialog wegschmeissen, da ich eingeloggt bin.
                prepareLogScreen(LOGINDLGSTATE_NORMAL);
            }
        }
    };
    OnCompleteListener<AuthResult> onCompEmail = new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            // If sign in fails, display a message to the user. If sign in succeeds
            // the auth state listener will be notified and logic to handle the
            // signed in user can be handled in the listener.
            if (!task.isSuccessful()) {
                Toast.makeText(getApplicationContext(), getString(R.string.auth_failed_tech) + " Fehlermeldung: " + task.getException(),
                        Toast.LENGTH_SHORT).show();
                ProgrssBarVisible(false);
            } else {
                // geschafft;
                // jetz muss der benutzernamen noch geändert werden...
                FirebaseUser user = mAuth.getCurrentUser();
                String umail = user.getEmail();
                String[] mailEx = umail.split("@");
                if (mailEx.length > 0) {
                    umail = mailEx[0];
                }
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(umail)
                        // könnte so auch das phono neu machen
                        //  .setPhotoUri(Uri.parse("https://example.com/jane-q-user/profile.jpg"))
                        .build();
                user.updateProfile(profileUpdates)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                ProgrssBarVisible(false);
                                if (!task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), getString(R.string.auth_register_fail_name) + " Fehlermeldung: " + task.getException(),
                                            Toast.LENGTH_SHORT).show();
                                }
                                prepareLogScreen(LOGINDLGSTATE_NORMAL);
                            }
                        });
            }


        }
    };

    @Override
    public void onBackPressed() {
        if (logSuc) {
            // zurück erlauben...
            super.onBackPressed();
        } else
            prepareLogScreen(LOGINDLGSTATE_NORMAL);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
            if (authStateListener != null) {
                mAuth.addAuthStateListener(authStateListener);
            }
        }
    }

    @Override
    public void onStop() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
            if (authStateListener != null) {
                mAuth.removeAuthStateListener(authStateListener);
            }
        }
        super.onStop();
    }

    private void crossfade(View v1, View v2) {
        final View vi2 = v2;
        // Set the content view to 0% opacity but visible, so that it is visible
        // (but fully transparent) during the animation.
        v1.setAlpha(0f);
        v1.setVisibility(View.VISIBLE);

        // Animate the content view to 100% opacity, and clear any animation
        // listener set on the view.
        v1.animate()
                .alpha(1f)
                .setDuration(mShortAnimationDuration)
                .setListener(null);

        // Animate the loading view to 0% opacity. After the animation ends,
        // set its visibility to GONE as an optimization step (it won't
        // participate in layout passes, etc.)
        vi2.animate()
                .alpha(0f)
                .setDuration(mShortAnimationDuration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        vi2.setVisibility(View.GONE);
                    }
                });
    }

    public void prepareLogScreen(int loginstate) {
        FirebaseUser user = mAuth.getCurrentUser();
        View child = null;
        // wenn das fenster das erste mal aufgerufen wird...
        if ((user != null)
                && (loginstate != LOGINDLGSTATE_NORMAL_FORCEIT)
                && (loginstate != LOGINDLGSTATE_SHOWEMAIL)
                && (loginstate != LOGINDLGSTATE_EMAILREGISTRATION)) {
            logState = LOGINDLGSTATE_LOGGEDIN;
            // nach firebird provider entscheiden,
            // welche userdaten angezeigt werrdne
            for (UserInfo profile : user.getProviderData()) {
                if (cameFromLogScreen) {
                    Intent eIntent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(eIntent);
                    onBackPressed();
                } else {

                    signInWelcome.setText(getResources().getString(R.string.auth_welcome_loggedIN));
                    child = this.getLayoutInflater().inflate(R.layout.activity_login_signmethod_angemneldetals, null);
                    rLayout.addView(child);
                    crossfade(child, rLayout.getChildAt(0));
                    rLayout.removeViewAt(0);
                    rootView.findViewById(R.id.log_out_button).setOnClickListener(this);
                    rootView.findViewById(R.id.log_out_button_neuAnmelden).setOnClickListener(this);
                    // die view mit den daten füllen.
                    new LoginImageHelper().fillLoginInfoField(this, rootView);

                    // Buttons switchen damit man sich neu anmelden kann..
                    Button rabmelden = (Button) rootView.findViewById(R.id.log_out_button);
                    rabmelden.setVisibility(View.VISIBLE);
                    if (user.isAnonymous()) {
                        Button rneuAnm = (Button) rootView.findViewById(R.id.log_out_button_neuAnmelden);
                        rneuAnm.setText(getString(R.string.auth_new_loginAnonym));
                        textUpperHint.setText(getString(R.string.auth_choose_methhint_loggedanonym));
                    } else {
                        // Welceh anmeldung - google email
                        textUpperHint.setText(getString(R.string.auth_choose_methhint_loggedin));
                    }
                }
            }
            ;
        } else {
            if (logState != loginstate) {
                logState = loginstate;
                switch (loginstate) {
                    case LOGINDLGSTATE_NORMAL:
                    case LOGINDLGSTATE_NORMAL_FORCEIT:
                        child = this.getLayoutInflater().inflate(R.layout.activity_login_signmethod_main, null);
                        if (user == null) {
                            signInWelcome.setText(getResources().getString(R.string.auth_choose_method));
                        } else {
                            backToProfileBtt = (Button) child.findViewById(R.id.sign_in_button_toProfile);
                            backToProfileBtt.setVisibility(View.VISIBLE);
                            signInWelcome.setText(getResources().getString(R.string.auth_new_login));
                        }
                        textUpperHint.setText(getString(R.string.auth_choose_methhint));
                        rLayout.addView(child);
                        crossfade(child, rLayout.getChildAt(0));
                        rLayout.removeViewAt(0);
                        rootView.findViewById(R.id.sign_in_button_google).setOnClickListener(this);
                        rootView.findViewById(R.id.sign_in_button_newaccount).setOnClickListener(this);
                        rootView.findViewById(R.id.sign_in_button_email).setOnClickListener(this);
                        rootView.findViewById(R.id.sign_in_button_toProfile).setOnClickListener(this);
                        break;
                    case LOGINDLGSTATE_SHOWEMAIL:
                        signInWelcome.setText(getResources().getString(R.string.autch_welcome_main_email));
                        child = this.getLayoutInflater().inflate(R.layout.activity_login_signmethod_email, null);
                        textUpperHint.setText(getString(R.string.auth_choose_method_emailhint));
                        rLayout.addView(child);
                        crossfade(child, rLayout.getChildAt(0));
                        rLayout.removeViewAt(0);
                        // email login form
                        rootView.findViewById(R.id.btn_reset_password).setOnClickListener(this);
                        rootView.findViewById(R.id.sign_up_button).setOnClickListener(this);
                        rootView.findViewById(R.id.navigate_back).setOnClickListener(this);
                        break;
                    case LOGINDLGSTATE_EMAILREGISTRATION:
                        signInWelcome.setText(getResources().getString(R.string.autch_welcome_main_remailreg));
                        child = this.getLayoutInflater().inflate(R.layout.activity_login_signmethod_emailregister, null);
                        textUpperHint.setText(getString(R.string.auth_choose_method_emailregisterhint));
                        rLayout.addView(child);
                        crossfade(child, rLayout.getChildAt(0));
                        rLayout.removeViewAt(0);
                        //register
                        rootView.findViewById(R.id.register_button).setOnClickListener(this);
                        rootView.findViewById(R.id.register_navigate_back).setOnClickListener(this);
                        break;
                }
            }
        }
    }

    // GOOGLE LOGIN....
    // Zeige google Login fenster intent...
    public void prepareGoogleFirebirdAuth() {
        mAuth = FirebaseAuth.getInstance();
        logSuc = mAuth.getCurrentUser() != null;
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    logSuc = true;
                } else {
                    // User is signed out
                    logSuc = false;
                }
                // ...
            }
        };
        mAuth.addAuthStateListener(authStateListener);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this) //***
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(getApplicationContext(), getString(R.string.auth_failed_tech_googleconn),
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        // für stillen login
        if (silentSingIn) {
            ProgrssBarVisible(true);
            mAuth.signInAnonymously()
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "signInAnonymously:onComplete:" + task.isSuccessful());
                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (!task.isSuccessful()) {
                                // meldung, bitte manuell anmelden...
                                Log.w(TAG, "signInAnonymously", task.getException());
                                Toast.makeText(getApplicationContext(), getString(R.string.auth_failed_anonymous) + " Fehlermeldung: " + task.getException(),
                                        Toast.LENGTH_SHORT).show();
                                ProgrssBarVisible(false);
                            }
                        }
                    });
        }
    }

    // as on activity result gibt mirr die ANtwort vom google intent.
    // ist es der Intent mit dem Requestcode RC_SIGN_IN
    // dann war es mein google anmeldeintent.
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                // VORSICHT, noch nicht die UI updaten, das kommt nach dem firebase login
                // UND ZWAR IM FUKKING LISTENER!!!
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                // Todo wenn es kein abort war, bitte vernünftiger fehlr!
                // ...
                //     Toast.makeText(getActivity(), getString(R.string.auth_failed_tech_google)+" Fehlermeldung: "+task.getException(),
                //            Toast.LENGTH_SHORT).show();
                ProgrssBarVisible(false);
            }
        }
    }

    // [START signOut]

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        googleCredential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        FirebaseUser user = mAuth.getCurrentUser();
        if ((user != null) && (user.isAnonymous())) {
            mAuth.getCurrentUser().linkWithCredential(googleCredential)
                    .addOnCompleteListener(this, onGoogleSignInComp);
        } else {
            if (user != null) {
                // erstmal ausloggen
                ResultCallback<Status> signOutCallback = new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        FirebaseAuth.getInstance().signOut();
                        mAuth.signInWithCredential(googleCredential)
                                .addOnCompleteListener(LoginActivity.this, onGoogleSignInComp);
                    }
                };
                signOut(signOutCallback);
            } else {
                mAuth.signInWithCredential(googleCredential)
                        .addOnCompleteListener(this, onGoogleSignInComp);
            }


        }


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getActivity().requestWindowFeature(Window.FEATURE_NO_TITLE);

        getIntent().getBooleanExtra("anonymLogin", silentSingIn);
        getIntent().getBooleanExtra("cameFromLogScreen", cameFromLogScreen);
        setContentView(R.layout.activity_login_signmethod);
        rootView = findViewById(android.R.id.content);
        progressBarLinLay = new ProgressDialog(this);
        progressBarLinLay.setCancelable(false);
        textUpperHint = (TextView) rootView.findViewById(R.id.auth_choose_method_hint);
        signInWelcome = (TextView) rootView.findViewById(R.id.auth_choose_methodwelcome);
        manin_loginscrren = (LinearLayout) rootView.findViewById(R.id.manin_login_scrren);

        googleBtt = (SignInButton) rootView.findViewById(R.id.sign_in_button_google);
        signinwelcome = (LinearLayout) rootView.findViewById(R.id.signin_welcome);
        log_out_btt = (Button) rootView.findViewById(R.id.log_out_button);
        regBtt = (Button) rootView.findViewById(R.id.register_button);
        MailLoginBtt = (Button) rootView.findViewById(R.id.sign_up_button);
        rLayout = (LinearLayout) rootView.findViewById(R.id.activity_login_signmethod_root);
        mainContent = (LinearLayout) rootView.findViewById(R.id.mainLoginDLGContent);


        logState = -1;
        prepareGoogleFirebirdAuth(); // VOR PEREPARE VIEW:..
        prepareLogScreen(LOGINDLGSTATE_NORMAL);
    }

    // [START signIn]
    private void signIn() {
        ProgrssBarVisible(true);
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public boolean showMessageCbAutoLogin() {
        View v = this.getLayoutInflater().inflate(R.layout.gigger_splash, null);
        cb = (CheckBox) v.findViewById(R.id.checkbox);
        cb.setText(getString(R.string.auth_really_loglout_anonymous_understand));
        cb.setVisibility(View.VISIBLE);
        cb.setChecked(false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.auth_really_loglout_anonymoustitle));
        builder.setMessage(getResources().getString(R.string.auth_really_loglout_anonymous))
                .setView(v)
                .setCancelable(false)
                .setNegativeButton(getString(R.string.btt_abort2), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(getString(R.string.btt_ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (cb.isChecked()) {
                            signOut(classicSignOutCallback);
                            dialog.dismiss();
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    getString(R.string.auth_really_loglout_anonymous_haken),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
        builder.show();

        return true;
    }

    private void signOut(ResultCallback<Status> cb) {
        ProgrssBarVisible(true);
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(cb);

    }

    // [START revokeAccess]
    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]

                        // [END_EXCLUDE]
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button_google:
                // lkogin via google
                signIn();
                break;
            case R.id.sign_in_button_email:
                // login via email...
                prepareLogScreen(LOGINDLGSTATE_SHOWEMAIL);
                break;
            case R.id.sign_in_button_newaccount:
                // neuer email account
                prepareLogScreen(LOGINDLGSTATE_EMAILREGISTRATION);
                break;
            // email form
            case R.id.sign_up_button:
                // login per email...
                login_email();
                break;
            case R.id.btn_reset_password:
                // reset password email...-.
                passwordEmail();
                break;
            case R.id.log_out_button:
                // reset password email...-.
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if ((user.isAnonymous())) {
                    showMessageCbAutoLogin();
                } else {
                    signOut(classicSignOutCallback);
                }
                break;
            case R.id.navigate_back:
            case R.id.log_out_button_neuAnmelden:
                // raus aus derm email login.
                prepareLogScreen(LOGINDLGSTATE_NORMAL_FORCEIT);
                break;
            case R.id.register_button:
                register();
                break;
            case R.id.register_navigate_back:
            case R.id.sign_in_button_toProfile:
                // raus aus registrieren.-.-
                prepareLogScreen(LOGINDLGSTATE_NORMAL);
                break;
        }
    }

    public void login_email() {
        final EditText ipass = (EditText) rootView.findViewById(R.id.password);
        final EditText logEmail = (EditText) rootView.findViewById(R.id.email);
        String logPass = ipass.getText().toString().trim();
        String email = logEmail.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            logEmail.setError(getString(R.string.auth_enter_email_anmeldung));
            return;
        }

        if (TextUtils.isEmpty(logPass)) {
            ipass.setError(getString(R.string.auth_enter_pass));
            return;
        }

        ProgrssBarVisible(true);
        //authenticate user
        mAuth.signInWithEmailAndPassword(email, logPass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        ProgrssBarVisible(false);
                        // TODO fehlermeldung, wenn man nicht mit dem Internet verbunden ist und sich gerade einlofgen will
                        if (!task.isSuccessful()) {
                            // there was an error
                            Toast.makeText(getApplicationContext(), getString(R.string.auth_failed) + " Fehlermeldung: " + task.getException(), Toast.LENGTH_LONG).show();
                        } else {
                            // geschafft;
                            prepareLogScreen(LOGINDLGSTATE_NORMAL);
                        }
                    }
                });
    }

    public void passwordEmail() {
        final EditText iemail = (EditText) rootView.findViewById(R.id.email);
        final String email = iemail.getText().toString().trim();

        if ((TextUtils.indexOf(email, "@") == -1)) {
            iemail.setError(getString(R.string.auth_enter_email));
            return;
        }

        ProgrssBarVisible(true);
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        ProgrssBarVisible(false);
                        if (task.isSuccessful()) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                            builder.setMessage(getString(R.string.auth_sendemail))
                                    .setCancelable(false)
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            //do things
                                        }
                                    });
                            AlertDialog alert = builder.create();
                            alert.show();
                        } else {
                            Toast.makeText(getApplicationContext(), getString(R.string.auth_sendemail_fail), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void register() {
        // neue mail registrieren.
        final EditText iemail = (EditText) rootView.findViewById(R.id.register_email);
        final EditText ipass = (EditText) rootView.findViewById(R.id.register_password);
        final EditText ipass2 = (EditText) rootView.findViewById(R.id.register_passwordwh);
        email = iemail.getText().toString().trim();
        password = ipass.getText().toString().trim();
        String password2 = ipass.getText().toString().trim();
        if ((TextUtils.indexOf(email, "@") == -1)) {
            iemail.setError(getString(R.string.auth_enter_email));
            return;
        }

        if (TextUtils.isEmpty(password)) {
            ipass.setError(getString(R.string.auth_enter_pass));
            return;
        }

        if (TextUtils.isEmpty(password)) {
            ipass.setError(getString(R.string.auth_enter_pass_Notthesame));
            return;
        }

        if (password.length() < 6) {
            ipass.setError(getString(R.string.auth_pass_short));
            return;
        }
        if (!password.equals(password2)) {
            ipass.setError(getString(R.string.auth_enter_pass_Notthesame));
            ipass.setText("");
            ipass2.setText("");
            return;
        }

        ProgrssBarVisible(true);


        FirebaseUser user = mAuth.getCurrentUser();
        if ((user != null) && (user.isAnonymous())) {
            AuthCredential credential = EmailAuthProvider.getCredential(email, password);
            mAuth.getCurrentUser().linkWithCredential(credential)
                    .addOnCompleteListener(this, onCompEmail);
        } else {
            if (user != null) {
                // erstmal ausloggen
                ResultCallback<Status> signOutCallback = new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        FirebaseAuth.getInstance().signOut();
                        mAuth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(LoginActivity.this, onCompEmail);
                    }
                };
                signOut(signOutCallback);
            } else {
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, onCompEmail);
            }
        }
    }

    public void ProgrssBarVisible(boolean vis) {
        if (vis) {
            progressBarLinLay.setMessage(getString(R.string.auth_loading));
            progressBarLinLay.show();
        } else {
            progressBarLinLay.dismiss();
        }
    }


}
