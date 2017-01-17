package net.livingrecordings.giggermainapp.EquipEditor;

import android.app.Dialog;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;


import net.livingrecordings.giggermainapp.R;
import net.livingrecordings.giggermainapp.giggerMainClasses.helperClasses.GiggerIntentHelperClass;
import net.livingrecordings.giggermainapp.giggerMainClasses.helperClasses.ItemImageCasheHelper;
import net.livingrecordings.giggermainapp.giggerMainClasses.helperClasses.ItemInterfaceHelper;

import static android.content.Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP;
import static net.livingrecordings.giggermainapp.giggerMainClasses.helperClasses.GiggerIntentHelperClass.equipIdent_ITEM;

/**
 * Created by Franky on 05.11.2016.
 */

public class EquipShowItemFragment extends Fragment {

    public String itemIdent;
    ItemInterfaceHelper thisItem;
    public EquipShowItemFragment() {
    }

    private Dialog showPicInDIalog(){
        Dialog settingsDialog = new Dialog(getActivity());
        settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View imgLayout = getActivity().getLayoutInflater().inflate(R.layout.fragment_equipeditor_showitem_imagefullscreen
                , null);
        ImageView img = (ImageView)imgLayout.findViewById(R.id.dialogFullscreenImgView);
        ItemImageCasheHelper.getInstance().loadImage_Cashed(getActivity(),img,thisItem.getKey(),thisItem.getCurrentItem().getGalleryPic());
        settingsDialog.setContentView(imgLayout);
        return settingsDialog;
    }

    private View.OnClickListener pictureClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // gallerierequest
            showPicInDIalog().show();
        }
    };

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = null;
        getActivity().setTitle("");
        rootView = inflater.inflate(R.layout.fragment_equipeditor_showitem, container, false);

        com.github.clans.fab.FloatingActionButton fab = (com.github.clans.fab.FloatingActionButton) rootView.findViewById(R.id.fab_editItem);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // jetzt will ich das viel aufklappt..
                if (itemIdent != null) {
                    GiggerIntentHelperClass ghc = new GiggerIntentHelperClass(getActivity());
                    ghc.intentEditItem(itemIdent);
                }
            }
        });

        // intent flag kann so aussen
        //  (uebergabe.equals("ITEM,NEW") || uebergabe.equals("ITEM,EDIT"))
        Intent eIntent = getActivity().getIntent();
        if ((eIntent != null) && eIntent.hasExtra(equipIdent_ITEM) && (!eIntent.getStringExtra(equipIdent_ITEM).isEmpty()) ) {
            itemIdent = eIntent.getStringExtra(equipIdent_ITEM);// z.b. Verstärker a
            thisItem = new ItemInterfaceHelper(getActivity(), rootView, itemIdent);

        } else {
            getActivity().finishActivity(FLAG_ACTIVITY_PREVIOUS_IS_TOP);
        }

        ImageView gallPicBtt = (ImageView) rootView.findViewById(R.id.viewItemImg);
        gallPicBtt.setOnClickListener(pictureClick);


        setHasOptionsMenu(true);
        return rootView;
    }

    // hier nuzr noch für options menü
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_showequip_del, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.title_EquipEditorActivity_delEquip) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}