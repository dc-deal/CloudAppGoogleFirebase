package net.livingrecordings.giggermainapp.LoginScreens;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;


import net.livingrecordings.giggermainapp.MainActivity;
import net.livingrecordings.giggermainapp.R;
import net.livingrecordings.giggermainapp.giggerMainClasses.BitmapConverterHelper;
import net.livingrecordings.giggermainapp.giggerMainClasses.GiggerIntentHelperClass;


import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.TimingLogger;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StreamDownloadTask;
import com.tokenautocomplete.TokenCompleteTextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
    TokenSearch searchEdit;
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

    View.OnClickListener ocL2 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("TestGround");
            timings.addSplit("PostToFirebase");
            for (int i = 0; i < recordCount; i++) {
                DatabaseReference ch = dbRef.push();
                dbRef.child(ch.getKey()).setValue(arrRecords.get(i));
            }
            timings.dumpToLog();
        }
    };
    View.OnClickListener ocL4 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("TestGround2");
            Map<String, Object> names = new HashMap();
            names.put("John", "John");
            names.put("Tim", "Tim");
            names.put("Sam", "Sam");
            names.put("Ben", "Ben");
            dbRef.push().updateChildren(names);
        }
    };

    View.OnClickListener ocL3 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            GiggerIntentHelperClass.getInstance(mContext).intentCreateItem("");
       //     Intent is = new Intent(mContext,ChipsSearch.class);
       //     mContext.startActivity(is);
        }
    };

    View.OnClickListener ocL5 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
                 Intent is = new Intent(mContext,MainActivity.class);
                 mContext.startActivity(is);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        timings = new TimingLogger(TAG, "Memory");
        arrRecords = new ArrayList();
        setContentView(R.layout.gigger_splash);

        Button crRecords = (Button) this.findViewById(R.id.button1);
        crRecords.setOnClickListener(ocL);
//        Button crItemBtt = (Button) this.findViewById(R.id.crItemBtt);
//        crItemBtt.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                GiggerIntentHelperClass.getInstance(getApplicationContext()).intentCreateItem("");
//            }
//        });
        Button crPostRecords = (Button) this.findViewById(R.id.button2);
        crPostRecords.setOnClickListener(ocL5);
        Button btt10 = (Button) this.findViewById(R.id.button10);
        btt10.setOnClickListener(ocL4);
        final Button searchst = (Button) this.findViewById(R.id.button3);
        searchst.setOnClickListener(ocL3);

        //  searchEdit = (EditText)this.findViewById(R.id.searchTag);
        listView = (ListView) this.findViewById(R.id.resList);



        dbRef = FirebaseDatabase.getInstance().getReference().child("TestGround");
        searchEdit = (TokenSearch) findViewById(R.id.searchTag);
        searchEdit.setIndexRef(dbRef);
        // suche starten..


        //        Query q1 = dbRef.limitToFirst(200);

   //     if (savedInstanceState == null) {
   //        searchEdit.setPrefix("Tags: ");
   ///     }
        //



//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
//        if (!prefs.getBoolean("first_time", false)) {
//            // FIRST TIME!!
//            furtherToMainActivity(true);
//        } else {
//            // again....
//            furtherToMainActivity(false);
//        }
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

    public void furtherToMainActivity(Boolean withSplash) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            if (withSplash) {
                setContent();
                /* New Handler to start the Menu-Activity
                * and close this Splash-Screen after some seconds.*/
                handler.sendEmptyMessageDelayed(0, SPLASH_DISPLAY_LENGTH);
            } else {
                startMainActivity();
            }

        } else {
            showQuestion();
        }
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

    public void loginGoogle() {
        // die hilfsfunktionen von der Googlelogin unit nutzen, um sich bei google anzumelden.
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