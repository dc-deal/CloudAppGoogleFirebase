package net.livingrecordings.giggermainapp.EquipEditor;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;


import net.livingrecordings.giggermainapp.R;
import net.livingrecordings.giggermainapp.giggerMainClasses.interfaceHelperClasses.InterfaceHelperCallbacks;
import net.livingrecordings.giggermainapp.giggerMainClasses.interfaceHelperClasses.InterfaceHelperRootClass;
import net.livingrecordings.giggermainapp.giggerMainClasses.interfaceHelperClasses.ItemInterfaceHelper;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.app.Activity.RESULT_OK;
import static net.livingrecordings.giggermainapp.giggerMainClasses.helperClasses.GiggerIntentHelperClass.equipIdent_ITEM;

/**
 * Created by Kraetzig Neu on 04.11.2016.
 */

public class EquipEditItemFragment extends Fragment
        implements InterfaceHelperCallbacks {

    public EquipEditItemFragment() {
    }

    static final int REQUEST_TAKE_PHOTO = 1;
    static final int GALLERY_REQUEST = 2;

    Boolean isNewEntry;
    ItemInterfaceHelper thisItem;
    Menu MyMenu;


    String mCurrentPhotoPath; // für das vollbildphoto...
    Uri photoURI;


    private Dialog createChoosePicDialog(){
        String[] items = {
                getString(R.string.title_EquipEditorActivity_bildEinladen),
                getString(R.string.title_EquipEditorActivity_bildGallery)};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.title_EquipEditorActivity_bildtitle)
               // .setMessage(R.string.picture_choose_click)
                .setItems(items, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        dispatchTakePictureIntent();
                                        break;
                                    case 1:
                                        dispatchChooseGalleryIntent();
                                }
                            }
                        }
                );
        // Create the AlertDialog object and return it
        return builder.create();
    }

    private View.OnClickListener pictureClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // gallerierequest
            createChoosePicDialog().show();
        }
    };

    private View.OnClickListener galleryClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // gallerierequest
            dispatchChooseGalleryIntent();
        }
    };

    private void dispatchChooseGalleryIntent(){
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GALLERY_REQUEST);
    }

    private View.OnClickListener campicClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // Kamera intent
            // Image anzeigen, falls ein da ist.
            // später soll das ein Liste sein.
            // TODO https://guides.codepath.com/android/implementing-a-horizontal-listview-guide#adding-twowayview-to-layout
            // TODO https://developer.android.com/training/camera/photobasics.html
            dispatchTakePictureIntent();
        }
    };

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        File imageFile = null;
        try {
            imageFile = createImageFile();
        } catch (IOException ex) {
            // Error occurred while creating the File
        }
        // Continue only if the File was successfully created
        if (imageFile != null) {

            Uri imageFileUri = Uri.fromFile(imageFile); // convert path to Uri
            photoURI = FileProvider.getUriForFile(getActivity(),
                        "net.livingrecordings.giggermainapp.fileprovider",
                    imageFile);
            photoURI = imageFileUri;
            Intent it = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            it.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, imageFileUri);
            startActivityForResult(it, REQUEST_TAKE_PHOTO);
        }
    }

    public void save(){
        thisItem.saveItemFromInterface();
    }

    public View.OnClickListener onCrClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            save();
        }
    };


    public void onSaveProgressComplete(){
        // erst jetzt.
        getActivity().onBackPressed();
    }

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
            }
            // klasse intialisiert die view mit dem Weten aus dem schlüssel des Gegenstandes.
            thisItem = new ItemInterfaceHelper();
            thisItem.setupItemInterfaceHelper(getActivity(), rootView, itemIdent);
            thisItem.setItemInterfaceHelperCallbacks(this);
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
        AppCompatActivity app = (AppCompatActivity) getActivity();
        app.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_discard);
        setHasOptionsMenu(true);
        ImageButton imageButton = (ImageButton) rootView.findViewById(R.id.viewItemImg);
        imageButton.setOnClickListener(pictureClick);

        // both invisible for now...
        Button gallPicBtt = (Button) rootView.findViewById(R.id.imageLoadButton_gallery);
        gallPicBtt.setOnClickListener(galleryClick);
        Button camPicBtt = (Button) rootView.findViewById(R.id.imageloadButton_camera);
        camPicBtt.setOnClickListener(campicClick);

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            thisItem.putGalleryUri(photoURI);
        }
        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
            // After Ok code.
            Uri imgUri = data.getData();
            thisItem.putGalleryUri(imgUri);
        }
    }


    // hier nuzr noch für options menü
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.editequip_menu, menu);
        this.MyMenu = menu;

        if (isNewEntry) {
            // dann noch das speichern lassen und das erstellen hinzufügen
            MenuItem item = MyMenu.findItem(R.id.equip_save);
            item.setTitle(getString(R.string.add_new_short));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.equip_save) {
            save();
        }

        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
        }


        return super.onOptionsItemSelected(item);
    }

}
