package net.livingrecordings.giggermainapp.BandEditor;

import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import net.livingrecordings.giggermainapp.R;
import net.livingrecordings.giggermainapp.giggerMainClasses.GiggerContactCollection;


import static net.livingrecordings.giggermainapp.giggerMainClasses.GiggerIntentHelperClass.bandIdent_Band;

/**
 * Created by Kraetzig Neu on 10.11.2016.
 */

public class BandEditorEditFragment extends Fragment {

    public View rootView;
    GiggerContactCollection gv;
    GiggerContactCollection.GiggerBand gBand;

    public BandEditorEditFragment() {

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_bandeditor_editband, container, false);
        Intent eIntent = getActivity().getIntent();

        if (eIntent != null && eIntent.hasExtra(bandIdent_Band)) {
            String bandIdent = eIntent.getStringExtra(bandIdent_Band);// z.b. Verstärker a
            getActivity().setTitle(getActivity().getResources().getString((R.string.editBand)));
            // band aus datenbank holen
            gv = new GiggerContactCollection();
            gBand = gv.bands.getBandById(bandIdent);
            // felder beschriften
            TextView bname = (TextView) getActivity().findViewById(R.id.banddetail_name);
            bname.setText(gBand.contactName);
            TextView bstyle = (TextView) getActivity().findViewById(R.id.banddetail_style);
            bstyle.setText(gBand.description);
            ImageView bImage = (ImageView) getActivity().findViewById(R.id.bandDetail_image);
            bImage.setImageBitmap(gBand.getimageBig(getActivity()));
        } else {
            // neue BAND!!!!!
            getActivity().setTitle(getActivity().getResources().getString(R.string.crBand));

        }


        return rootView;
    }

}
