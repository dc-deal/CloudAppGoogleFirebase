package net.livingrecordings.giggermainapp;

/**
 * Created by Kraetzig Neu on 02.11.2016.
 */

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import net.livingrecordings.giggermainapp.giggerMainClasses.GiggerIntentHelperClass;

import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;


import static net.livingrecordings.giggermainapp.giggerMainClasses.GiggerIntentHelperClass.equipIdent_CATEGORY;


public class FragmentMainEquipListFragment extends Fragment
implements FragmentMainEquipListManager.catManagerCallbacks {

    // TODO .. wenn man den bildschirm rotiert, sieht man den progressdialog nicht mehr
    // TODO UND!!! Leider sieht man auch nicht mehr die

    public Context rootContext;

    public ListView androidGridView;
    public View rootView;
    public Bundle savedBundle;
    public String catIdent = "root";
    public String parCatFromPopView;
    // FAB zusatz menü
    public FloatingActionMenu menuRed;
    FragmentMainEquipListManager adapter;
    int gridClicked;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        rootContext = getActivity().getApplicationContext();
        savedBundle = savedInstanceState;
        if (rootContext != null) { // null check when fragment is detached
            rootView = inflater.inflate(R.layout.fragment_maincategorygrid, container, false);
            // ---------------------------------------------------
            // FAB LISTENER
            // Listener für den Add button unten im Bild..
            // z.b. einen menüpuinkt hinzufügen...
//            final FloatingActionButton programFab1 = new FloatingActionButton(getActivity());
//            programFab1.setButtonSize(FloatingActionButton.SIZE_MINI);
//            programFab1.setLabelText(getString(R.string.lorem_ipsum));
//            programFab1.setImageResource(R.drawable.ic_edit);
            menuRed = (FloatingActionMenu) rootView.findViewById(R.id.create_new_Equip_or_cat);
            FloatingActionButton crItemFAB = (FloatingActionButton) rootView.findViewById(R.id.crItemFAB);
            FloatingActionButton crCatFAB = (FloatingActionButton) rootView.findViewById(R.id.crCatFAB);
            //  menuRed.addMenuButton(programFab1);
            crCatFAB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    GiggerIntentHelperClass ghc = new GiggerIntentHelperClass(getActivity());
                    ghc.intentCreateCat();
                    menuRed.close(true);
                }
            });
            crItemFAB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    GiggerIntentHelperClass ghc = new GiggerIntentHelperClass(getActivity());
                    ghc.intentCreateItem("");
                    menuRed.close(true);
                }
            });

            androidGridView = (ListView) rootView.findViewById(R.id.maincategorylist_gridview);
            adapter = new FragmentMainEquipListManager(getActivity(),this,androidGridView,"root");
            registerForContextMenu(androidGridView);

        }
        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onLastLevelClicked(String actCatKey) {
        new GiggerIntentHelperClass(getActivity()).intentShowItemList(actCatKey);
    }

    public void pushBack(){
        if (adapter != null) {
            adapter.pushBack();
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info =
                (AdapterView.AdapterContextMenuInfo) menuInfo;
        int gridClicked = info.position;
        final ContextMenu menu1 = menu;

        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.mainequiplist_contextmenu, menu);
        // die kategorie herausfinden, die geklickt wurde.

        final String searchKey = adapter.getRef(gridClicked).getKey();
        adapter.getRef(gridClicked).orderByChild("parCat").equalTo(searchKey).addValueEventListener(new ValueEventListener() {
            @Override public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() <= 0) {
                    MenuItem mi = menu1.findItem(R.id.crItem1);
                    mi.setVisible(true);
                }
            } @Override public void onCancelled(DatabaseError databaseError) {}
        });

        super.onCreateContextMenu(menu, v, menuInfo);
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        GiggerIntentHelperClass ghc = new GiggerIntentHelperClass(getActivity());
        switch (item.getItemId()) {
            case R.id.editCat:
                Log.i("ContextMenu", "Item editCat was chosen");
                ghc.intentEditCat(adapter.getRef(gridClicked).getKey());
                return true;
            case R.id.delCat:
                Log.i("ContextMenu", "Item delCat was chosen");
                adapter.getRef(gridClicked).removeValue(new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
          //              FirebaseStorage.getInstance().getReferenceFromUrl(adapter.getItem(gridClicked).getImg()).delete();
                        Toast.makeText(getContext(),getString(R.string.editcat_fail_input),Toast.LENGTH_LONG).show();
                    }
                });
                return true;
            case R.id.catCategoryManager:
                Log.i("ContextMenu", "Item catCategoryManager was chosen");
                return true;
            case R.id.crItem1:
                Log.i("ContextMenu", "Item crItem was chosen");
                ghc.intentCreateItem(adapter.getRef(gridClicked).getKey());
                return true;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putString(equipIdent_CATEGORY, this.catIdent);
    }

    @Override
    public void onResume() {
        super.onResume();
        // set adapter nortmalerweise..
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}