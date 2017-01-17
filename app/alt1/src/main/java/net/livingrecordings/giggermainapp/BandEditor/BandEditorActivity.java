package net.livingrecordings.giggermainapp.BandEditor;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import net.livingrecordings.giggermainapp.EquipEditor.EquipShowItemFragment;
import net.livingrecordings.giggermainapp.R;

import static net.livingrecordings.giggermainapp.giggerMainClasses.GiggerIntentHelperClass.EditorMode_DETAIL;
import static net.livingrecordings.giggermainapp.giggerMainClasses.GiggerIntentHelperClass.EditorMode_EDIT;
import static net.livingrecordings.giggermainapp.giggerMainClasses.GiggerIntentHelperClass.GHC_mode;


/**
 * Created by Kraetzig Neu on 10.11.2016.
 */

public class BandEditorActivity extends FragmentActivity {

    public String showMode;
    public String equipType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_globaleditor);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        setTitle("");
        showMode = "";
        Intent eIntent = this.getIntent();
        if (eIntent != null && eIntent.hasExtra(GHC_mode)) {
            showMode = eIntent.getStringExtra(GHC_mode); // EDIT,DETAIL
            if (showMode.equals(EditorMode_DETAIL)) {
                // zeige details zu der band an.
                ft.replace(R.id.placeholder_editfragment, new BandShowFragment());
                ft.commit();
                // logik hier weil kein fragment vorhanden ist.
            } else if (showMode.equals(EditorMode_EDIT)) {
                ft.replace(R.id.placeholder_editfragment, new BandEditorEditFragment());
                ft.commit();
            }
        }
    }


}
