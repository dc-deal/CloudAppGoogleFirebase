package net.livingrecordings.giggermainapp.EquipEditor;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;


import net.livingrecordings.giggermainapp.R;

import static android.app.Activity.RESULT_OK;
import static net.livingrecordings.giggermainapp.giggerMainClasses.GiggerIntentHelperClass.equipIdent_CATEGORY;
import static net.livingrecordings.giggermainapp.giggerMainClasses.GiggerIntentHelperClass.equipIdent_ITEM;

/**
 * Created by Kraetzig Neu on 04.11.2016.
 */

public class EquipEditItemFragment extends Fragment {

    public EquipEditItemFragment() {
    }
//
//    static final int REQUEST_IMAGE_CAPTURE = 1;
//
//    private void dispatchTakePictureIntent() {
//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
//            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
//        }
//    }

    static final int REQUEST_TAKE_PHOTO = 1;
    static final int GALLERY_REQUEST = 1;


    String mCurrentPhotoPath,parCat;
    Boolean isNewEntry;
    ItemClassInterfaceHelper thisItem;
    Menu MyMenu;


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
    }

    public String[] catNames;
    ImageView showPic;


    public View.OnClickListener onCrClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            thisItem.saveItem();
        }
    };

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = null;
        getActivity().setTitle("");
        rootView = inflater.inflate(R.layout.fragment_equipeditor_edititem, container, false);
        Intent eIntent = getActivity().getIntent();
        if (eIntent != null && eIntent.hasExtra(equipIdent_ITEM)) {
            String itemIdent = eIntent.getStringExtra(equipIdent_ITEM);// z.b. Verstärker a
            isNewEntry = (itemIdent.isEmpty());
            if (isNewEntry) {
                // den button anzeigen...
                Button newEntryBtt = (Button) rootView.findViewById(R.id.new_entry_button);
                newEntryBtt.setVisibility(View.VISIBLE);
                newEntryBtt.setOnClickListener(onCrClick);
            };


            parCat = eIntent.getStringExtra(equipIdent_CATEGORY);
            // klasse intialisiert die view mit dem Weten aus dem schlüssel des Gegenstandes.
            thisItem =  new ItemClassInterfaceHelper(getActivity(),rootView,itemIdent,parCat);
            // spinner einstellen
            // kategorie des aktuellen items wird gesucht
            if (isNewEntry) {
                getActivity().setTitle(getResources().getString(R.string.title_EquipEditorActivity_crItem));
                // spinner einstellen,
                // Kategorie des herkunfts- intents wird genommen.
            } else {
                getActivity().setTitle(getResources().getString(R.string.title_EquipEditorActivity_editItem));
                // die klasse kann das UI selber füllen....
            }

        }

       // button kamera

        AppCompatButton btt = (AppCompatButton)rootView.findViewById(R.id.imageloadButton_camera);
        btt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        AppCompatButton galleryBTT = (AppCompatButton)rootView.findViewById(R.id.imageLoadButton_gallery);
        btt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // bild aus gallerie laden...
                // und dynamisch der liste hinzuügen ..
            }
        });
        // Image anzeigen, falls ein da ist.
        // später soll das ein Liste sein.
        //https://guides.codepath.com/android/implementing-a-horizontal-listview-guide#adding-twowayview-to-layout
        // TODO https://developer.android.com/training/camera/photobasics.html
    //    showPic = (ImageView)rootView.findViewById(R.id.viewItemImg);
    //    showPic.setVisibility(View.INVISIBLE);
        // befindet sich shcon ein bild für
        //if ()
        AppCompatActivity app = (AppCompatActivity)getActivity();
        app.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_discard);
        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            showPic.setImageBitmap(imageBitmap);
            showPic.setVisibility(View.VISIBLE);
            // TODO .. hier müssen bilder hinzugefügt werden können für ddie zusätzlich en sachen..
            // thisItem.putAdditiionalPIC
        }
        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
            // After Ok code.
            Uri imgUri = data.getData();
            thisItem.putGalleryUri(imgUri);
        };
    }


    // hier nuzr noch für options menü
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.editequip_menu, menu);
        this.MyMenu = menu;

        if (isNewEntry) {
            // dann noch das speichern lassen und das erstellen hinzufügen
            MenuItem item = MyMenu.findItem(R.id.equip_save);
            item.setVisible(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.equip_save) {
            thisItem.saveItem();
        }

        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
        }


        return super.onOptionsItemSelected(item);
    }

}
