package net.livingrecordings.giggermainapp.BandEditor;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import net.livingrecordings.giggermainapp.R;
import net.livingrecordings.giggermainapp.giggerMainClasses.GiggerContactCollection;
import net.livingrecordings.giggermainapp.giggerMainClasses.helperClasses.GiggerIntentHelperClass;

/**
 * Created by Kraetzig Neu on 11.11.2016.
 */

public class ImportSingleContactActivity extends AppCompatActivity {

    GiggerContactCollection gc;
    GiggerIntentHelperClass gih;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_importsinglecontact);
        setTitle(this.getResources().getString(R.string.importcontact_header));

        gc = new GiggerContactCollection();
        gih = new GiggerIntentHelperClass(this);

        EditText myName = (EditText) this.findViewById(R.id.import_contacts_searchTextView);
        myName.setText("");

//        ListView myList = (ListView)this.findViewById(R.id.import_contacts_listview);
//        // hier deie onlineliste machen, und einen listenen auf das textfeld machen der die datenmende durchsucht.
//        ImportSingleContactListAdapter arra = new ImportSingleContactListAdapter(this,gc.allContacts);
//        myList.setAdapter(arra);
//
//        myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            public void onItemClick(AdapterView<?> parent, View view,
//                                    int position, long id) {
//                gih.intentShowContact(gc.allContacts.get(position));
//
//            }
//        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //    getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
//
//        if (id == R.id.main_menu_action_settings) {
//            return true;
//        }
        return super.onOptionsItemSelected(item);
    }



}
