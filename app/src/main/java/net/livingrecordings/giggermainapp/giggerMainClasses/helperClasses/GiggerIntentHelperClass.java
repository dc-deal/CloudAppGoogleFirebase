package net.livingrecordings.giggermainapp.giggerMainClasses.helperClasses;

import android.content.Context;
import android.content.Intent;
import android.util.Log;


import net.livingrecordings.giggermainapp.BandEditor.BandEditorActivity;
import net.livingrecordings.giggermainapp.BandEditor.EditMyProfile;
import net.livingrecordings.giggermainapp.BandEditor.ShowContactDetailActivity;
import net.livingrecordings.giggermainapp.EquipEditor.EquipEditorActivity;
import net.livingrecordings.giggermainapp.LoginScreens.LoginActivity;
import net.livingrecordings.giggermainapp.MainActivity;
import net.livingrecordings.giggermainapp.giggerMainClasses.MainAPI.GiggerMainAPI;


/**
 * Created by Kraetzig Neu on 11.11.2016.
 */

public class GiggerIntentHelperClass {


    // in dieser Klasse habe ich alle meine Intents zusammen, als befehle...

    // Intent const hier für den EDITOR!!
    public static String EditorMode_DETAIL = "DETAIL";
    public static String EditorMode_EDIT = "EDIT";
    public static String GHC_Type = "equipType";
    public static String GHC_mode = "mode";
    public static String EditorEquipType_ITEM = "ITEM";
    public static String EditorEquipType_CATEGORY = "CATEGORY";
    // Intent Constants .. IDS
    public static String equipIdent_ITEM = "itemIdent";
    public static String equipIdent_CATEGORY = "catIdent";

    // Bandeditor
    public static String bandIdent_Band = "bandIdent";

    // --------------------------------------------------
    // Kategorien und items.

    public Context mContext;

    public GiggerIntentHelperClass(Context inp) {
        this.mContext = inp;
    }


    public static GiggerIntentHelperClass getInstance(Context cont) {
        return new GiggerIntentHelperClass(cont);
    }


    private Boolean checkActivity() {
        Boolean res = false;
        if (mContext == null) {
            Log.e("HCLASSE", "Fehler, der Helferklasse wurde keine Activity zugewiesen");
            res = true;
        }
        return res;
    }


    private void prepareIntentEQEditor(String ghcmode, String ghctype, String itemIdent, String ClassIdent){
            Intent eIntent = new Intent(mContext, EquipEditorActivity.class);
            eIntent.putExtra(GHC_Type, ghctype);// ITEM,CATEGORY
            eIntent.putExtra(equipIdent_ITEM, itemIdent); // leer weil neu.
            eIntent.putExtra(equipIdent_CATEGORY, ClassIdent);
            eIntent.putExtra(GHC_mode, ghcmode);  // EDIT,DETAIL
            if (ghcmode.equals(EditorMode_DETAIL)) {
                eIntent.setFlags(eIntent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
            }
            mContext.startActivity(eIntent);
    }

//    private void ButtonClickBySani(int ButtonId, final Class<? extends Activity> ActivityToOpen)
//    {
//        Button btn;
//        // Locate the button in activity_main.xml
//        btn = (Button) findViewById(ButtonId);
//
//        // Capture button clicks
//        btn.setOnClickListener(new OnClickListener() {
//            public void onClick(View arg0) {
//                startActivity(new Intent(getBaseContext(), ActivityToOpen));
//                // Start NewActivity.class
//                //Intent myIntent = new Intent(getBaseContext(), ActivityToOpen);
//                // startActivity(myIntent);
//            }
//        });
//    }


    public void intentEditItem(String forItemIdent) {
        prepareIntentEQEditor(EditorMode_EDIT,EditorEquipType_ITEM,forItemIdent,"");
    }

    public void intentEditCat(String forCategoryIdent) {
        prepareIntentEQEditor(EditorMode_EDIT,EditorEquipType_CATEGORY,"",forCategoryIdent);
    }

    public void intentCreateItem(String forCategoryIdent) {
        prepareIntentEQEditor(EditorMode_EDIT,EditorEquipType_ITEM,"",forCategoryIdent);
    }

    public void intentMainActivity() {
        Intent is = new Intent(mContext,MainActivity.class);
        mContext.startActivity(is);
    }
    public void intentCreateCat() {
        prepareIntentEQEditor(EditorMode_EDIT,EditorEquipType_CATEGORY,"","");
    }

    public void intentShowItem(String itemIdent) {
        prepareIntentEQEditor(EditorMode_DETAIL,EditorEquipType_ITEM,itemIdent,"");
    }


    public static String BandEditorType_BAND = "BAND";
    public static String contactIdent_BAND = "identBand";
    public static String contactIdent_CONTACT = "identContact";

    private void prepareIntentBandEditor(String ghcmode, String ghctype, String bandIdent){
            Intent eIntent = new Intent(mContext, BandEditorActivity.class);
            eIntent.putExtra(GHC_mode, ghcmode);
            eIntent.putExtra(GHC_Type, ghctype);
            eIntent.putExtra(contactIdent_BAND, bandIdent);
            mContext.startActivity(eIntent);
    }

    public void intentShowContact(String uid) {
        Intent eIntent = new Intent(mContext, ShowContactDetailActivity.class);
        eIntent.putExtra(contactIdent_CONTACT, uid);
        mContext.startActivity(eIntent);
    }

    public void intentShowProfile(String uid) {
        Intent eIntent = new Intent(mContext, ShowContactDetailActivity.class);
        eIntent.putExtra(contactIdent_CONTACT, uid);
        mContext.startActivity(eIntent);
    }

    public void intentEditProfile() {
        GiggerMainAPI api = GiggerMainAPI.getInstance();
        if (!api.isAnonymous()) {
            Intent eIntent = new Intent(mContext, EditMyProfile.class);
            eIntent.putExtra(contactIdent_CONTACT, api.getCurrentUserUID());
            mContext.startActivity(eIntent);
        }
    }

    public void intentEditBand(String gb) {
        prepareIntentBandEditor(EditorMode_EDIT,BandEditorType_BAND,gb);
    }



    public void intentShowBand(String bandIdent) {
        prepareIntentBandEditor(EditorMode_DETAIL,BandEditorType_BAND,bandIdent);
    }

    public void intentCreateBand() {
        prepareIntentBandEditor(EditorMode_EDIT,BandEditorType_BAND,"");
    }



    public void intentShowLogin(){
        Intent eIntent = new Intent(mContext, LoginActivity.class);
        mContext.startActivity(eIntent);
    }
}
