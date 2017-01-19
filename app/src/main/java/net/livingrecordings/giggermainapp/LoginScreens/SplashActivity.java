package net.livingrecordings.giggermainapp.LoginScreens;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;


import net.livingrecordings.giggermainapp.MainActivity;
import net.livingrecordings.giggermainapp.R;
import net.livingrecordings.giggermainapp.giggerMainClasses.helperClasses.BitmapConverterHelper;
import net.livingrecordings.giggermainapp.giggerMainClasses.helperClasses.GiggerIntentHelperClass;


import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.TimingLogger;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


/**
 * Created by Kraetzig Neu on 21.11.2016.
 */

public class SplashActivity extends AppCompatActivity{

    public static final String TAG = "UPLOADTEST";
    // class variable
    final String lexicon = "ABCDEFGHIJKLMNOPQRSTUVWXYZ12345674890";
    final java.util.Random rand = new java.util.Random();
    // consider using a Map<String,Boolean> to say whether the identifier is being used or not
    final Set<String> identifiers = new HashSet<String>();
    final int recordCount = 10000;
    // EditText searchEdit;
    Context mContext;
    ListView listView;
    String searchT;
    DatabaseReference dbRef;
    /**
     * Duration of wait
     **/
    private final int SPLASH_DISPLAY_LENGTH = 0;
    FragmentManager fragmentManager;
    CheckBox cb;
    //----------------------------------
    TimingLogger timings;
    ArrayList<String> arrRecords;
    View.OnClickListener ocL = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            timings.addSplit("Create In memory");
            for (int i = 0; i < recordCount; i++) {
                arrRecords.add(randomIdentifier());
            }
            timings.dumpToLog();
        }
    };

    View.OnClickListener bttSplash4ClickLogin = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            GiggerIntentHelperClass.getInstance(mContext).intentShowLogin();
        }
    };

    View.OnClickListener bttSplash1Click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            GiggerIntentHelperClass.getInstance(mContext).intentCreateItem("");
            //     Intent is = new Intent(mContext,ChipsSearch.class);
            //     mContext.startActivity(is);
        }
    };


    View.OnClickListener bttSplash3MainActivityClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            GiggerIntentHelperClass.getInstance(mContext).intentMainActivity();
            //     Intent is = new Intent(mContext,ChipsSearch.class);
            //     mContext.startActivity(is);
        }
    };

    private LoginActivity dialogFragment;
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            startMainActivity();
        }
    };

    public String randomIdentifier() {
        StringBuilder builder = new StringBuilder();
        while (builder.toString().length() == 0) {
            int length = rand.nextInt(5) + 5;
            for (int i = 0; i < length; i++) {
                builder.append(lexicon.charAt(rand.nextInt(lexicon.length())));
            }
            if (identifiers.contains(builder.toString())) {
                builder = new StringBuilder();
            }
        }
        return builder.toString();
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public void checkIfLoggedIn(){
        // "default loginworkflow"
        //-------------------------------
        FirebaseAuth.getInstance().addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseDatabase.getInstance().setPersistenceEnabled(true);
                Boolean uThere = FirebaseAuth.getInstance().getCurrentUser() != null;
                if (uThere) {
                    // ALL OK!
                    //  bttSplash1Click.onClick(null);
                    //bttSplash2itemlist.onClick(this.findViewById(R.id.bttSplash2)); // zu dem items.
                    handler.sendEmptyMessageDelayed(0, SPLASH_DISPLAY_LENGTH);
                }else {
                    showQuestion();
                }
            }
        });
    }
    public void noInternetWorkflowExecute(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.splashcbTitle));
        builder.setMessage(getResources().getString(R.string.splachcbText))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.retry), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (isOnline()){
                            dialog.dismiss();
                            checkIfLoggedIn();
                        }
                    }
                })
                .setNegativeButton(getString(R.string.btt_abort), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        getApplication().onTerminate();
                    }
                });
        builder.show();
    }


    private void mainLoginWorkflow(){
        // der "no internet workflow"
        // IMMER ZUM SCHLUSS!!!
        // -------------------------
        if (!isOnline()){
            noInternetWorkflowExecute();
        } else {
            // internet da...
            checkIfLoggedIn();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;


//        Button crPostRecords = (Button) this.findViewById(R.id.bttSplash4);
//        crPostRecords.setOnClickListener(bttSplash4ClickLogin);
//        Button btt10 = (Button) this.findViewById(R.id.bttSplash3);
//        btt10.setOnClickListener(bttSplash3MainActivityClick);
//        Button searchst = (Button) this.findViewById(R.id.bttSplash1);
//        searchst.setOnClickListener(bttSplash1Click);
//        final Button btt2 = (Button) this.findViewById(R.id.bttSplash2);
//        btt2.setOnClickListener(bttSplash2itemlist);


        setContent();
        mainLoginWorkflow();
//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
//        if (!prefs.getBoolean("first_time", false)) {
    }



    public void setContent() {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();
        setContentView(R.layout.gigger_splash);
        ImageView splashImg = (ImageView) findViewById(R.id.splash_img);
        splashImg.setVisibility(View.GONE);

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        BitmapConverterHelper bh = new BitmapConverterHelper();//
        splashImg.setImageBitmap(bh.resizeBitmap(getResources(), R.drawable.gigger_splash_wtitle1, Math.round(dpHeight)));
        splashImg.setVisibility(View.VISIBLE);
    }


    public void startMainActivity() {
        Intent eIntent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(eIntent);
        finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("first_time", true);
        editor.commit();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void showQuestion() {
        View v = getLayoutInflater().inflate(R.layout.gigger_splash, null);
        cb = (CheckBox) v.findViewById(R.id.checkbox);
        cb.setVisibility(View.VISIBLE);
        cb.setChecked(true);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.splashcbTitle));
        builder.setMessage(getResources().getString(R.string.splachcbText))
                .setView(v)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        showDialog(cb.isChecked());
                    }
                });
        builder.show();
    }

    public void showDialog(boolean anonymLogin) {
        // erster login bildschirm und rechte
        Intent LoginIntent = new Intent(this, LoginActivity.class);
        Bundle mBundle = new Bundle();
        mBundle.putBoolean("anonymLogin", anonymLogin);
        mBundle.putBoolean("cameFromLogScreen", true);
        LoginIntent.putExtras(mBundle);
        this.startActivity(LoginIntent);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            // After Ok code.

        } else if (resultCode == Activity.RESULT_CANCELED) {
            // After Cancel code.
        }
    }


}