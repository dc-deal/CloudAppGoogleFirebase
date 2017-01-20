package net.livingrecordings.giggermainapp.BandEditor;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import net.livingrecordings.giggermainapp.R;
import net.livingrecordings.giggermainapp.giggerMainClasses.helperClasses.GiggerIntentHelperClass;

import static net.livingrecordings.giggermainapp.giggerMainClasses.helperClasses.GiggerIntentHelperClass.contactIdent_CONTACT;

/**
 * Created by Kraetzig Neu on 20.01.2017.
 */

public class EditMyProfile extends AppCompatActivity {

    private String contIdent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editprofile);

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



        }
    }
}
