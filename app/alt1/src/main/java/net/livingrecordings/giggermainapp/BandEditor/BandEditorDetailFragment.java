package net.livingrecordings.giggermainapp.BandEditor;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.livingrecordings.giggermainapp.R;

import static net.livingrecordings.giggermainapp.giggerMainClasses.GiggerIntentHelperClass.bandIdent_Band;


/**
 * Created by Kraetzig Neu on 10.11.2016.
 */

public class BandEditorDetailFragment extends Fragment {

    public BandEditorDetailFragment() {

    }

    public View rootView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_bandeditor_showband, container, false);


        Intent eIntent = getActivity().getIntent();
        if (eIntent != null && eIntent.hasExtra(bandIdent_Band)) {
            String bandIdent = eIntent.getStringExtra(bandIdent_Band);// z.b. Verstärker a

            // band aus der Datenmenge holen

            // band darstellen im Layout.

        }

        return rootView;
    }

}
