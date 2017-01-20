package net.livingrecordings.giggermainapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionMenu;

import net.livingrecordings.giggermainapp.BandEditor.BandList;

import net.livingrecordings.giggermainapp.giggerMainClasses.helperClasses.GiggerIntentHelperClass;

/**
 * Created by Kraetzig Neu on 02.11.2016.
 */


public class BandListFragment extends Fragment implements BandList.BandListCallbacks {

    FloatingActionMenu fabMenu;

    GiggerIntentHelperClass ghc;
    BandList bl;
    TextView noBands;
    ProgressBar progBar;

    public void onEmptyDatabase(){
       // hier werrde ich anzeigen wenn noch keine bands gemacht wurden...
       noBands.setVisibility(View.VISIBLE);
    }

    public void onReadyLoading(){
        // hier werrde ich anzeigen wenn noch keine bands gemacht wurden...
        progBar.setVisibility(View.GONE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_mainbandlist, container, false);
        noBands = (TextView) rootView.findViewById(R.id.no_bands_inList);
        progBar = (ProgressBar) rootView.findViewById(R.id.bands_progressbar);

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
                fabMenu.close(true);
            }
        });
        ghc = new GiggerIntentHelperClass(getActivity());


        ListView list = (ListView) rootView.findViewById(R.id.maincontactslist_fragment);
        bl = new BandList();
        progBar.setVisibility(View.VISIBLE);
        bl.startBandList(getActivity(),list,true);
        bl.setBandListCallbacks(this);
        //gcc.bands <- hiermit kannn ic nun alles darstellen


        return rootView;
    }
}