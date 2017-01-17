package net.livingrecordings.giggermainapp.giggerMainClasses;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseUser;

import net.livingrecordings.giggermainapp.BandEditor.BandEditorActivity;
import net.livingrecordings.giggermainapp.BandEditor.ImportSingleContactActivity;
import net.livingrecordings.giggermainapp.BandEditor.ShowContactDetailActivity;
import net.livingrecordings.giggermainapp.EquipEditor.EquipEditorActivity;
import net.livingrecordings.giggermainapp.ItemListActivity;


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

    public Context fragActivityVar;

    public GiggerIntentHelperClass(Context inp) {
        this.fragActivityVar = inp;
    }


    public static GiggerIntentHelperClass getInstance(Context cont) {
        return new GiggerIntentHelperClass(cont);
    }


    private Boolean checkActivity() {
        Boolean res = false;
        if (fragActivityVar == null) {
            Log.e("HCLASSE", "Fehler, der Helferklasse wurde keine Activity zugewiesen");
            res = true;
        }
        return res;
    }

    private boolean checkInputParam(Object inp) {
        Boolean res = true;
        if ((checkActivity()) || (inp == null)) {
            Toast.makeText(fragActivityVar, "Fehler beim Aufrufen des Formulars, Eingangsvariablen Leer.", Toast.LENGTH_LONG);
            res = false;
        }
        return res;
    }

    private void prepareIntentEQEditor(String ghcmode, String ghctype, String itemIdent, String ClassIdent){
        if (checkInputParam(itemIdent) && checkInputParam(ClassIdent)) {
            Intent eIntent = new Intent(fragActivityVar, EquipEditorActivity.class);
            eIntent.putExtra(GHC_Type, ghctype);// ITEM,CATEGORY
            eIntent.putExtra(equipIdent_ITEM, itemIdent); // leer weil neu.
            eIntent.putExtra(equipIdent_CATEGORY, ClassIdent);
            eIntent.putExtra(GHC_mode, ghcmode);  // EDIT,DETAIL
            fragActivityVar.startActivity(eIntent);
        }
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

    public void intentCreateCat() {
        prepareIntentEQEditor(EditorMode_EDIT,EditorEquipType_CATEGORY,"","");
    }

    public void intentShowItem(String itemIdent) {
        prepareIntentEQEditor(EditorMode_DETAIL,EditorEquipType_ITEM,itemIdent,"");
    }


    public void intentShowItemList(String forCategoryIdent) {
        if (checkInputParam(forCategoryIdent)) {
            Intent eIntent = new Intent(fragActivityVar, ItemListActivity.class);
            eIntent.putExtra(equipIdent_CATEGORY, forCategoryIdent);
            fragActivityVar.startActivity(eIntent);
        }

    }


    public static String BandEditorType_BAND = "BAND";
    public static String contactIdent_BAND = "identBand";
    public static String contactIdent_CONTACT = "identContact";

    private void prepareIntentBandEditor(String ghcmode, String ghctype, String bandIdent){
        if (checkInputParam(bandIdent)) {
            Intent eIntent = new Intent(fragActivityVar, BandEditorActivity.class);
            eIntent.putExtra(GHC_mode, ghcmode);
            eIntent.putExtra(GHC_Type, ghctype);
            eIntent.putExtra(contactIdent_BAND, bandIdent);
            fragActivityVar.startActivity(eIntent);
        }
    }

    public void intentShowContact(GiggerContactCollection.GiggerContact gc) {
        Intent eIntent = new Intent(fragActivityVar, ShowContactDetailActivity.class);
        eIntent.putExtra(contactIdent_CONTACT, gc.contactID);
        fragActivityVar.startActivity(eIntent);
    }

    public void intentShowFBContasct(FirebaseUser fbUser) {
        Intent eIntent = new Intent(fragActivityVar, ShowContactDetailActivity.class);
        eIntent.putExtra(contactIdent_CONTACT, fbUser.getUid());
        fragActivityVar.startActivity(eIntent);
    }

    public void intentEditBand(GiggerContactCollection.GiggerBand gb) {
        prepareIntentBandEditor(EditorMode_EDIT,BandEditorType_BAND,gb.contactID);
    }
    public void intentShowBand(String bandIdent) {
        prepareIntentBandEditor(EditorMode_DETAIL,BandEditorType_BAND,bandIdent);
    }

    public void intentCreateBand() {
        prepareIntentBandEditor(EditorMode_EDIT,BandEditorType_BAND,"");
    }

    public void intentCreateContact(){
        Intent eIntent = new Intent(fragActivityVar, ImportSingleContactActivity.class);
        fragActivityVar.startActivity(eIntent);
    }
}
