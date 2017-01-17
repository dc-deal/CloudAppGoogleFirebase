package net.livingrecordings.giggermainapp;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import net.livingrecordings.giggermainapp.giggerMainClasses.helperClasses.GiggerIntentHelperClass;

/**
 * Created by Kraetzig Neu on 04.11.2016.
 */

public class ItemListActivity extends ActionBarActivity{


    public String forCategoryIdent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itemmentlist);
    }


    // hier nuzr noch für options menü
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.itemlist_menu, menu);
        // Fragment einladen.

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.crEquip) {
            GiggerIntentHelperClass gch = new GiggerIntentHelperClass(this);
            gch.intentCreateItem(forCategoryIdent); // welche kategorie denn!?
        }
        if (id == R.id.equipManager) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
