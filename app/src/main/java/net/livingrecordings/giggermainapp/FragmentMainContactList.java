package net.livingrecordings.giggermainapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.github.clans.fab.FloatingActionMenu;

import net.livingrecordings.giggermainapp.giggerMainClasses.GiggerContactCollection;
import net.livingrecordings.giggermainapp.giggerMainClasses.helperClasses.GiggerIntentHelperClass;

/**
 * Created by Kraetzig Neu on 02.11.2016.
 */


public class FragmentMainContactList extends Fragment {

    FloatingActionMenu fabMenu;
    FragmentMainContactListAdapter adapterViewAndroid;
    GiggerContactCollection gcc;
    GiggerIntentHelperClass ghc;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_maincontactlist, container, false);
        //--------------------------------------------
        // Listener für den Add button unten im Bild..
        fabMenu = (FloatingActionMenu) rootView.findViewById(R.id.create_new_BandOrContact);
        com.github.clans.fab.FloatingActionButton crBand = (com.github.clans.fab.FloatingActionButton) rootView.findViewById(R.id.crBand);
        com.github.clans.fab.FloatingActionButton crContact = (com.github.clans.fab.FloatingActionButton) rootView.findViewById(R.id.crContact);
        //  menuRed.addMenuButton(programFab1);
        crBand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GiggerIntentHelperClass ghc = new GiggerIntentHelperClass(getActivity());
                ghc.intentCreateBand();
                fabMenu.close(true);
            }
        });
        crContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ghc.intentCreateContact();
                fabMenu.close(true);
            }
        });
        ghc = new GiggerIntentHelperClass(getActivity());
        gcc = new GiggerContactCollection();
        GiggerContactCollection.GiggerBandList bl = gcc.bands;
        // hier muss ich die Liste fertig machen.
        ExpandableListView bandList = (ExpandableListView)rootView.findViewById(R.id.maincontactslist_fragment);


        adapterViewAndroid = new FragmentMainContactListAdapter(getActivity(),gcc.bands);
        bandList.setAdapter(adapterViewAndroid);
        //gcc.bands <- hiermit kannn ic nun alles darstellen


        return rootView;
    }
}