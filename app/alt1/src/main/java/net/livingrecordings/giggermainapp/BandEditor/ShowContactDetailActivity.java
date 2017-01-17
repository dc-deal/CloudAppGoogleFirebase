package net.livingrecordings.giggermainapp.BandEditor;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import net.livingrecordings.giggermainapp.FragmentMainContactListAdapter;
import net.livingrecordings.giggermainapp.LoginScreens.LoginImageHelper;
import net.livingrecordings.giggermainapp.R;
import net.livingrecordings.giggermainapp.giggerMainClasses.GiggerContactCollection;
import net.livingrecordings.giggermainapp.giggerMainClasses.GiggerIntentHelperClass;

import static net.livingrecordings.giggermainapp.giggerMainClasses.GiggerIntentHelperClass.contactIdent_CONTACT;


/**
 * Created by Kraetzig Neu on 10.11.2016.
 */

public class ShowContactDetailActivity extends AppCompatActivity {

    public ShowContactDetailActivity() {

    }

    public String contIdent;
    public GiggerContactCollection gc;
    public GiggerContactCollection.GiggerContact gContact;
    GiggerIntentHelperClass ghc;
    FragmentMainContactListAdapter bandAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bandeditor_showcontact);

        com.github.clans.fab.FloatingActionButton fab = (com.github.clans.fab.FloatingActionButton) this.findViewById(R.id.fab_showcontact);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // jetzt will ich das viel aufklappt..
            }
        });

        GiggerIntentHelperClass ghc = new GiggerIntentHelperClass(this);
        Intent eIntent = this.getIntent();
        if (eIntent != null && eIntent.hasExtra(contactIdent_CONTACT)) {
            contIdent = eIntent.getStringExtra(contactIdent_CONTACT);// z.b. Verstärker a
            gc = new GiggerContactCollection();
            fab.setVisibility(View.INVISIBLE);
            this.setTitle(getResources().getString(R.string.bandEditor_contacts));
            if (contIdent.equals(gc.loginUser.contactID)) {
                // das ist der Profilbildschirm!
                fab.setVisibility(View.VISIBLE);
                // PROFIL!!
                this.setTitle(getResources().getString(R.string.myprofile_header));
            }

            TextView contStatus = (TextView) this.findViewById(R.id.contdetail_email);
            TextView contName = (TextView) this.findViewById(R.id.contdetail_name);
            ImageView profilePic = (ImageView) this.findViewById(R.id.contactDetail_image);

            gContact = gc.allContacts.getContactById(contIdent);
            if (gContact == null) {
                // probieren wirs mal mit firebgase auth ;)
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    contName.setText(user.getDisplayName());
                    contStatus.setText(user.getEmail());
                    new LoginImageHelper().fillImage(this,profilePic,user.getPhotoUrl());
                }
            } else {
                // band darstellen im Layout.
                profilePic.setImageBitmap(gContact.getimageBig(this));
                contName.setText(gContact.contactName);
                contStatus.setText(gContact.contactNumber);

                ExpandableListView bandList = (ExpandableListView) this.findViewById(R.id.contdetail_bandlist);
                bandAdapter = new FragmentMainContactListAdapter(this, gContact.myBands);
                bandList.setAdapter(bandAdapter);
            }

        }
    }

}
