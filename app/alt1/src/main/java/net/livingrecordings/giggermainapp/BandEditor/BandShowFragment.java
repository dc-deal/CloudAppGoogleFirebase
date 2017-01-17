package net.livingrecordings.giggermainapp.BandEditor;

import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import net.livingrecordings.giggermainapp.R;
import net.livingrecordings.giggermainapp.giggerMainClasses.GiggerContactCollection;
import net.livingrecordings.giggermainapp.giggerMainClasses.GiggerIntentHelperClass;

import static net.livingrecordings.giggermainapp.giggerMainClasses.GiggerIntentHelperClass.bandIdent_Band;

/**
 * Created by Kraetzig Neu on 10.11.2016.
 */

public class BandShowFragment extends Fragment {

    public View rootView;
    GiggerContactCollection gv;
    GiggerContactCollection.GiggerBand gBand;
    GiggerIntentHelperClass ghc;
    public BandShowFragment() {

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_bandeditor_showband, container, false);
        Intent eIntent = getActivity().getIntent();

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab_showband);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // jetzt will ich das viel aufklappt..
                if (gBand != null) {
                    ghc.intentEditBand(gBand);
                }
            }
        });

        GiggerIntentHelperClass ghc = new GiggerIntentHelperClass(getActivity());
        if (eIntent != null && eIntent.hasExtra(bandIdent_Band)) {
            String bandIdent = eIntent.getStringExtra(bandIdent_Band);// z.b. Verstärker a
            // band aus datenbank holen
            gv = new GiggerContactCollection();
            gBand = gv.bands.getBandById(bandIdent);
            // felder beschriften
            EditText bname = (EditText) getActivity().findViewById(R.id.bandEditor_name);
            bname.setText(gBand.contactName);
            EditText bstyle = (EditText) getActivity().findViewById(R.id.bandEditor_Style);
            bstyle.setText(gBand.description);
            ImageView bImage = (ImageView) getActivity().findViewById(R.id.bandEditor_logo);
            bImage.setImageBitmap(gBand.getimageBig(getActivity()));

            // darstellen.
        }
        return rootView;
    }

}
