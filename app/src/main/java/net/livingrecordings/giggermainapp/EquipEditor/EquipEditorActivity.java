package net.livingrecordings.giggermainapp.EquipEditor;

import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


import net.livingrecordings.giggermainapp.R;

import static net.livingrecordings.giggermainapp.giggerMainClasses.helperClasses.GiggerIntentHelperClass.EditorEquipType_CATEGORY;
import static net.livingrecordings.giggermainapp.giggerMainClasses.helperClasses.GiggerIntentHelperClass.EditorEquipType_ITEM;
import static net.livingrecordings.giggermainapp.giggerMainClasses.helperClasses.GiggerIntentHelperClass.EditorMode_DETAIL;
import static net.livingrecordings.giggermainapp.giggerMainClasses.helperClasses.GiggerIntentHelperClass.GHC_mode;
import static net.livingrecordings.giggermainapp.giggerMainClasses.helperClasses.GiggerIntentHelperClass.GHC_Type;

/**
 * Created by Kraetzig Neu on 04.11.2016.
 */

public class EquipEditorActivity extends AppCompatActivity {

    public String showMode;
    public String equipType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        getSupportActionBar().setElevation(0);
        setContentView(R.layout.activity_globaleditor);
        setTitle("");
        //getSupportActionBar().setElevation(0);
        Intent eIntent = this.getIntent();
        if (eIntent != null && eIntent.hasExtra(GHC_mode)) {
            equipType = eIntent.getStringExtra(GHC_Type); // ITEM,CATEGORY
            showMode = eIntent.getStringExtra(GHC_mode); // ITEM,CATEGORY
            if (showMode.equals(EditorMode_DETAIL) && equipType.equals(EditorEquipType_ITEM)) {
                //Zeige item im schönen coordinator layout
                ft.replace(R.id.placeholder_editfragment, new EquipShowItemFragment());
                ft.commit();

            } else if (showMode.equals(EditorMode_DETAIL) && equipType.equals(EditorEquipType_CATEGORY)) {
                // noch nix
            }
            // wenn der intent hier vorbei ist, ist es immer ien edit oder new.
            // was genau edit oder new wird dann im fragment entschieden!!
            else if (equipType.equals(EditorEquipType_ITEM)) {
                // Item bearbeiten oder neu
                ft.replace(R.id.placeholder_editfragment, new EquipEditItemFragment());
                ft.commit();
            } else if (equipType.equals(EditorEquipType_CATEGORY)) {
                // Kategorie bearbeiten oder neu.
           //     ft.replace(R.id.placeholder_editfragment, new editCategoryfragmentDepricated());
                ft.commit();
            }


        }
    }


}