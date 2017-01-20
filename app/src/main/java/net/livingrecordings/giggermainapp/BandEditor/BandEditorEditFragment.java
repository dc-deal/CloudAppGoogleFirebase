package net.livingrecordings.giggermainapp.BandEditor;

import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import net.livingrecordings.giggermainapp.R;
import net.livingrecordings.giggermainapp.giggerMainClasses.interfaceHelperClasses.BandInterfaceHelper;
import net.livingrecordings.giggermainapp.giggerMainClasses.interfaceHelperClasses.InterfaceHelperCallbacks;


import static net.livingrecordings.giggermainapp.giggerMainClasses.helperClasses.GiggerIntentHelperClass.bandIdent_Band;

/**
 * Created by Kraetzig Neu on 10.11.2016.
 */

public class BandEditorEditFragment extends Fragment implements InterfaceHelperCallbacks {

    public View rootView;
    BandInterfaceHelper bih;
    Boolean isNewEntry;
    Menu MyMenu;

    public BandEditorEditFragment() {

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_bandeditor_editband, container, false);
        Intent eIntent = getActivity().getIntent();
        isNewEntry = true;
        if (eIntent != null && eIntent.hasExtra(bandIdent_Band)) {
            String bandIdent = eIntent.getStringExtra(bandIdent_Band);// z.b. Verstärker a
            isNewEntry = (bandIdent.isEmpty());
            // band aus datenbank holen
            bih = BandInterfaceHelper.getInstance();
            bih.setupBandInterface(getActivity(),rootView,bandIdent);

            getActivity().setTitle(getResources().getString(R.string.editBand));
        } else {
            // neue BAND!!!!!
            getActivity().setTitle(getActivity().getResources().getString(R.string.crBand));
            getActivity().setTitle(getResources().getString(R.string.crBand));

            // den button anzeigen...
            Button newEntryBtt = (Button) rootView.findViewById(R.id.new_entry_button);
            newEntryBtt.setVisibility(View.VISIBLE);
            newEntryBtt.setOnClickListener(onCrClick);
        }


        return rootView;
    }

    public void save(){
        bih.saveItemFromInterface();
    }

    public View.OnClickListener onCrClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            save();
        }
    };


    public void onSaveProgressComplete(){
        // erst jetzt.
        getActivity().onBackPressed();
    }


    // hier nuzr noch für options menü
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.editequip_menu, menu);
        this.MyMenu = menu;

        if (isNewEntry) {
            // dann noch das speichern lassen und das erstellen hinzufügen
            MenuItem item = MyMenu.findItem(R.id.equip_save);
            item.setTitle(getString(R.string.add_new_short));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.equip_save) {
            save();
        }

        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
        }


        return super.onOptionsItemSelected(item);
    }
}
