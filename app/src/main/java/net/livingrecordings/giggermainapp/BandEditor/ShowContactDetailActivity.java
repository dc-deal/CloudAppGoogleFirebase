package net.livingrecordings.giggermainapp.BandEditor;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import net.livingrecordings.giggermainapp.LoginScreens.LoginImageHelper;
import net.livingrecordings.giggermainapp.R;
import net.livingrecordings.giggermainapp.giggerMainClasses.MainAPI.GiggerMainAPI;
import net.livingrecordings.giggermainapp.giggerMainClasses.helperClasses.GiggerIntentHelperClass;
import net.livingrecordings.giggermainapp.giggerMainClasses.models.UserClass;

import static net.livingrecordings.giggermainapp.giggerMainClasses.helperClasses.GiggerIntentHelperClass.contactIdent_CONTACT;


/**
 * Created by Kraetzig Neu on 10.11.2016.
 */

public class ShowContactDetailActivity extends AppCompatActivity implements BandList.BandListCallbacks {

    public ShowContactDetailActivity() {

    }

    public String contIdent;
    public GiggerMainAPI mAPI = GiggerMainAPI.getInstance();

    TextView contEmail;
    TextView contName;
    ImageView profilePic;
    BandList bandList;
    ListView list;

    public void onEmptyDatabase(){
        // hier werrde ich anzeigen wenn noch keine bands gemacht wurden...
    }
    public void onReadyLoading(){
        // hier werrde ich anzeigen wenn noch keine bands gemacht wurden...

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bandeditor_showcontact);

        com.github.clans.fab.FloatingActionButton fab = (com.github.clans.fab.FloatingActionButton) this.findViewById(R.id.fab_showcontact);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // jetzt will ich das viel aufklappt..
               GiggerIntentHelperClass.getInstance(ShowContactDetailActivity.this).intentEditProfile();
            }
        });

        Intent eIntent = this.getIntent();
        if (eIntent != null && eIntent.hasExtra(contactIdent_CONTACT)) {
            contIdent = eIntent.getStringExtra(contactIdent_CONTACT);// z.b. Verstärker a
            fab.setVisibility(View.INVISIBLE);
            this.setTitle(getResources().getString(R.string.bandEditor_contacts));
            if (contIdent.equals(mAPI.getCurrentUserUID())) {
                // das ist der Profilbildschirm!
                fab.setVisibility(View.VISIBLE);
                // PROFIL!!
                this.setTitle(getResources().getString(R.string.myprofile_header));
            }

            contEmail = (TextView) this.findViewById(R.id.contdetail_email);
            contName = (TextView) this.findViewById(R.id.contdetail_name);
            profilePic = (ImageView) this.findViewById(R.id.contactDetail_image);
            list = (ListView) this.findViewById(R.id.contdetail_bandlist);
            bandList = new BandList();
            mAPI.getUsersRef(contIdent).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    UserClass user = dataSnapshot.getValue(UserClass.class);
                    if (user != null) {
                        contName.setText(user.getName());
                        contEmail.setText(user.getEmail());
                        new LoginImageHelper().fillImage(ShowContactDetailActivity.this,profilePic, Uri.parse(user.getImgUrl()));

                        // band Liste..
                        bandList.startBandList(ShowContactDetailActivity.this,list,false);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });



        }
    }

}
