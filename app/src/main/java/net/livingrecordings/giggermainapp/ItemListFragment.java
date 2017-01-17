package net.livingrecordings.giggermainapp;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import net.livingrecordings.giggermainapp.giggerMainClasses.helperClasses.GiggerIntentHelperClass;
import net.livingrecordings.giggermainapp.giggerMainClasses.GiggerMainAPI;
import net.livingrecordings.giggermainapp.giggerMainClasses.models.ItemClass;
import net.livingrecordings.giggermainapp.giggerMainClasses.helperClasses.ItemImageCasheHelper;
import static net.livingrecordings.giggermainapp.giggerMainClasses.helperClasses.GiggerIntentHelperClass.equipIdent_CATEGORY;

/**
 * Created by Kraetzig Neu on 04.11.2016.
 */

public class ItemListFragment extends Fragment {

    public Context rootContext;
    String forTag;
    String itemIdent;
    public FirebaseListAdapter<ItemClass> listAdapter;
    ProgressBar progBarItemLoading;


    private void FillItemList_ItemView(ItemClass item, View view) {
        ((TextView) view.findViewById(R.id.itemlist_textview_name)).setText(item.getName());
        ((TextView) view.findViewById(R.id.itemlist_textview_desc)).setText(item.getDesc());
        // noch das bild... dafür den cashe anzapfen ( managed die unit automatisch)
        ItemImageCasheHelper.getInstance()
                .loadImage_Cashed(
                        getActivity(),
                        (ImageView) view.findViewById(R.id.itemlist_textview_image),
                        item.getDbKey(),item.getGalleryPic());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_itemlist, container, false);
        rootContext = getActivity().getApplicationContext();
        // Listener für den Add button unten im Bild..
        final com.github.clans.fab.FloatingActionMenu acMen =
                (com.github.clans.fab.FloatingActionMenu)rootView.findViewById(R.id.create_new_Item_ItemlistFragment);
        final com.github.clans.fab.FloatingActionButton crItemFAB =
                (com.github.clans.fab.FloatingActionButton) rootView.findViewById(R.id.crItemFAB);
        //  menuRed.addMenuButton(programFab1);
        crItemFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new GiggerIntentHelperClass(getActivity()).intentCreateItem(forTag);
                acMen.close(true);
            }
        });

        if (rootContext != null) { // null check when fragment is detached
            // grid view finden um den adapter zu binden.
            ListView iList = (ListView) rootView.findViewById(R.id.maincategorylist_listview_layer2);
            Intent eIntent = getActivity().getIntent();
            if (eIntent != null && eIntent.hasExtra(equipIdent_CATEGORY)) {
                forTag = eIntent.getStringExtra(equipIdent_CATEGORY);
                if (forTag.isEmpty()){
                    getActivity().setTitle(
                            getResources().getString(R.string.Equipment));
                } else {
                    getActivity().setTitle(
                            getResources().getString(R.string.title_EquipEditorActivity_showCat)
                                    + " " + forTag);
                }
            }

            GiggerMainAPI gi = GiggerMainAPI.getInstance();
            Query fbQ = gi.getCreatedByUserQuery();

            // check if empty..
            fbQ.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() == null) {
                        progBarItemLoading.setVisibility(View.GONE);
                        ((TextView) rootView.findViewById(R.id.maincategorylist_no_items_textview)).setVisibility(View.VISIBLE);
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

            progBarItemLoading = (ProgressBar)rootView.findViewById(R.id.items_progressbar);
            progBarItemLoading.setVisibility(View.VISIBLE);
            listAdapter = new FirebaseListAdapter<ItemClass>
                    (getActivity(), ItemClass.class, R.layout.items_itemlist_item,
                            fbQ)
            {
                @Override
                protected void populateView(View view, ItemClass object,int i) {
                    // Listitem füllen.
                    object.setDbKey(listAdapter.getRef(i).getKey());
                    FillItemList_ItemView(object,view);
                    progBarItemLoading.setVisibility(View.GONE);
                    ((TextView) rootView.findViewById(R.id.maincategorylist_no_items_textview)).setVisibility(View.GONE);
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
                GiggerMainAPI.getInstance().removeItem(itemIdent);
                return true;
            case R.id.equipManager:
                Log.i("ContextMenu", "Item 1b was chosen");
                return true;
        }
        return super.onContextItemSelected(item);
    }
}
