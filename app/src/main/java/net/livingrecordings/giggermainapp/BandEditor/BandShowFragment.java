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
import net.livingrecordings.giggermainapp.giggerMainClasses.helperClasses.GiggerIntentHelperClass;
import net.livingrecordings.giggermainapp.giggerMainClasses.interfaceHelperClasses.BandInterfaceHelper;

import static net.livingrecordings.giggermainapp.giggerMainClasses.helperClasses.GiggerIntentHelperClass.bandIdent_Band;

/**
 * Created by Kraetzig Neu on 10.11.2016.
 */

public class BandShowFragment extends Fragment {

    public View rootView;
    GiggerIntentHelperClass ghc;
    BandInterfaceHelper bih;
    public BandShowFragment() {

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_bandeditor_showband, container, false);
        Intent eIntent = getActivity().getIntent();

        if (eIntent != null && eIntent.hasExtra(bandIdent_Band)) {
            String bandIdent = eIntent.getStringExtra(bandIdent_Band);// z.b. Verstärker a
            bih = BandInterfaceHelper.getInstance();
            bih.setupBandInterface(getActivity(),rootView,bandIdent);


        }
        return rootView;
    }

}
