package net.livingrecordings.giggermainapp;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import net.livingrecordings.giggermainapp.giggerMainClasses.GiggerIntentHelperClass;
import net.livingrecordings.giggermainapp.giggerMainClasses.ItemClass;

import java.util.ArrayList;

import static net.livingrecordings.giggermainapp.giggerMainClasses.GiggerIntentHelperClass.equipIdent_CATEGORY;

/**
 * Created by Kraetzig Neu on 04.11.2016.
 */

public class ItemListFragment extends Fragment {

    public Context rootContext;
    String forCategoryIdent;
    String forCategoryName;
    String itemIdent;
    public FirebaseListAdapter<ItemClass> listAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_itemlist, container, false);
        rootContext = getActivity().getApplicationContext();
        // Listener für den Add button unten im Bild..
        com.github.clans.fab.FloatingActionButton crItemFAB = (com.github.clans.fab.FloatingActionButton) rootView.findViewById(R.id.crItemFAB);
        //  menuRed.addMenuButton(programFab1);
        crItemFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new GiggerIntentHelperClass(getActivity()).intentCreateItem(forCategoryIdent);
            }
        });

        if (rootContext != null) { // null check when fragment is detached
            // grid view finden um den adapter zu binden.
            ListView iList = (ListView) rootView.findViewById(R.id.maincategorylist_listview_layer2);
            Intent eIntent = getActivity().getIntent();
            if (eIntent != null && eIntent.hasExtra(equipIdent_CATEGORY)) {
                forCategoryIdent = eIntent.getStringExtra(equipIdent_CATEGORY);
                ItemListActivity parAct = (ItemListActivity)getActivity();
                parAct.forCategoryIdent = forCategoryIdent;

                // TODO Global konstante heir einfügen usercategories
                DatabaseReference itRef = FirebaseDatabase.getInstance()
                        .getReference("userCategoriesData")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(forCategoryIdent);


                itRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        forCategoryName = (String)dataSnapshot.child("name").getValue();
                        getActivity().setTitle(
                                getResources().getString(R.string.title_EquipEditorActivity_showCat)
                                        + " " + forCategoryName);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

            DatabaseReference fbItemsRef = FirebaseDatabase.getInstance()
                    .getReference("userItemsData")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child(forCategoryIdent);

//
//            if (fbNoItems <= 0) {
//                iList.setVisibility(View.INVISIBLE);
//                TextView equipDesc = (TextView) rootView.findViewById(R.id.maincategorylist_no_items_textview);
//                equipDesc.setVisibility(View.VISIBLE);
//            }

            listAdapter = new FirebaseListAdapter<ItemClass>
                    (getActivity(), ItemClass.class, R.layout.items_itemlist,
                            fbItemsRef.orderByChild("parCat").equalTo(forCategoryIdent))
            {
                @Override
                protected void populateView(View view, ItemClass object,int i) {
                    ((TextView)view.findViewById(R.id.txtviewItemsShow)).setText(object.getName());
                }
            };
            iList.setAdapter(listAdapter);

            registerForContextMenu(iList);
            // klick event
            iList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    new GiggerIntentHelperClass(getActivity())
                            .intentShowItem(listAdapter.getRef(position).getKey());
                }
            });
        }
        return rootView;
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.itemlist_contextmenu, menu);

        AdapterView.AdapterContextMenuInfo info =
                (AdapterView.AdapterContextMenuInfo) menuInfo;
        itemIdent = listAdapter.getRef(info.position).getKey();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        GiggerIntentHelperClass ghc = new GiggerIntentHelperClass(getActivity());
        switch (item.getItemId()) {
            case R.id.editEquip:
                ghc.intentEditItem(itemIdent);
                return true;
            case R.id.delEquip:
                Log.i("ContextMenu", "Item 1b was chosen");
                return true;
            case R.id.equipManager:
                Log.i("ContextMenu", "Item 1b was chosen");
                return true;
        }
        return super.onContextItemSelected(item);
    }
}
