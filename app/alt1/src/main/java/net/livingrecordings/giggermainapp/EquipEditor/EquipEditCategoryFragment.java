package net.livingrecordings.giggermainapp.EquipEditor;

import android.app.ProgressDialog;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import net.livingrecordings.giggermainapp.R;

import java.util.Date;

import static android.app.Activity.RESULT_OK;
import static net.livingrecordings.giggermainapp.giggerMainClasses.GiggerIntentHelperClass.equipIdent_CATEGORY;


/**
 * Created by Kraetzig Neu on 04.11.2016.
 */

public class EquipEditCategoryFragment extends Fragment
implements CategoryDlg.categoryDLGEvents {


    private final static int GALLERY_REQUEST = 1;

    public static Button catButton;
    static DatabaseReference userCats;

    ImageButton imageButton;
    View rootView = null;
    EditText catName;
    EditText equipDesc;
    Uri imgUri;
    Menu MyMenu;
    Boolean isNewEntry;
    Boolean imgChosen = false;
    FirebaseDatabase database;
    FirebaseStorage storage;
    StorageReference storageRef;
    FirebaseUser user;
    String catInput, chosenCategory;
    ProgressDialog progDlg,progDlgNormal;
    public View.OnClickListener onCrClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            saveAndQuit();
        }
    };



    @Override
    public void onChooseCategory(String choosenCatParKey){
        if (choosenCatParKey != null) {
            chosenCategory = choosenCatParKey;
            if (choosenCatParKey.equals("root")) {
                catButton.setText(getString(R.string.main_category));
            } else {
                userCats.child(chosenCategory).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        catButton.setText((String) dataSnapshot.child("name").getValue());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        }
    }



    public void saveAndQuit() {
        /// / PRÜFUNGEN!!!!
        final String catNameStr = catName.getText().toString().trim();
        final String catDescStr = equipDesc.getText().toString().trim();
        // imgUri
        if ((!catNameStr.equals("") && (!catDescStr.equals("") && (imgChosen)))) {
            // gigger service aufrufen...
          //  new GiggerItemAPI().
        }
    }

    public void updateSingleCatrecord(String key, String nam, String desc, String imglink, String cat) {
        userCats.child(key).child("name").setValue(nam);
        userCats.child(key).child("desc").setValue(desc);
        userCats.child(key).child("img").setValue(imglink);
        userCats.child(key).child("parCat").setValue(cat);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Dieses Fragmet stellt den Equipmenteditor da.
        // ich lade hier die anzeige einer kategorie oder eines Items ein.
        // dann kann ich noch entscheiden ob ich in den editiermodus gehe was wiederum andere
        // Dieses Fragmet stellt den Equipmenteditor da.
        // ich lade hier die anzeige einer kategorie oder eines Items ein.
        // dann kann ich noch entscheiden ob ich in den editiermodus gehe was wiederum andere
        getActivity().setTitle("");
        FirebaseAuth auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        progDlgNormal = new ProgressDialog(getActivity());
        progDlgNormal.setCancelable(false);
        progDlgNormal.setMessage(getString(R.string.upload_running_justSave));

        progDlg = new ProgressDialog(getActivity());
        progDlg.setCancelable(false);
        progDlg.setIndeterminate(false);
        progDlg.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progDlg.setMessage(getString(R.string.upload_running));
        database = FirebaseDatabase.getInstance();
        userCats = database.getReference("").child(user.getUid());
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference().child("").child(user.getUid());
        rootView = inflater.inflate(R.layout.fragment_equipeditor_editcategory, container, false);
        catName = (EditText) rootView.findViewById(R.id.EquipEditorActivity_editcatName);
        equipDesc = (EditText) rootView.findViewById(R.id.EquipEditorActivity_editcatDesc);
        catButton = (Button) rootView.findViewById(R.id.choose_cat_button);
        imageButton = (ImageButton) rootView.findViewById(R.id.catImageSelect);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST);
            }
        });

        // intent flag kann so aussen
        //  (uebergabe.equals("ITEM,NEW") || uebergabe.equals("ITEM,EDIT"))
        Intent eIntent = getActivity().getIntent();
        if (eIntent != null && eIntent.hasExtra(equipIdent_CATEGORY)) {
            catInput = eIntent.getStringExtra(equipIdent_CATEGORY);// z.b. Verstärker a
            isNewEntry = (catInput.isEmpty());
            if (isNewEntry) {
                catInput = "root";
                // hier bauch ich ersma nix machen alles soll leer sein.,,
                getActivity().setTitle(getResources().getString(R.string.title_EquipEditorActivity_crCat));
                // dann noch das speichern lassen und das erstellen hinzufügen
                Button newEntryBtt = (Button) rootView.findViewById(R.id.new_entry_button);
                newEntryBtt.setVisibility(View.VISIBLE);
                newEntryBtt.setOnClickListener(onCrClick);
            } else {
                getActivity().setTitle(getResources().getString(R.string.title_EquipEditorActivity_editCat));
                userCats.child(catInput).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        userCats.child(catInput).removeEventListener(this); // nicht mehr
                        catName.setText((String) dataSnapshot.child("name").getValue());
                        equipDesc.setText((String) dataSnapshot.child("desc").getValue());
                        onChooseCategory((String) dataSnapshot.child("chosenCategory").getValue());

                        imgUri = Uri.parse((String) dataSnapshot.child("img").getValue());
                        imgChosen = true; // only when ok...
                        Picasso.with(getActivity())
                                .load(imgUri)
                                .placeholder(R.drawable.imgplaceholder)// //R.drawable.progress_animation
                                .error(R.drawable.erroricon).into(imageButton);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }

        }
        AppCompatActivity app = (AppCompatActivity) getActivity();
        app.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_discard);
        setHasOptionsMenu(true);


        catButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                CategoryDlg dlg = new CategoryDlg();
                dlg.setTargetFragment(fragmentManager.getFragments().get(0),11);
                Bundle args = new Bundle();
                args.putString("startCategory", chosenCategory);
                dlg.setArguments(args);
                dlg.show(fragmentManager, "dialog");
            }
        });

        chosenCategory = catInput;
        onChooseCategory(catInput); // damit die kategorieansicht stimmt.
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
            // After Ok code.
            imgUri = data.getData();
            imgChosen = true; // only when ok...
            imageButton.setImageURI(imgUri);
        }
        ;
    }
    //----------------------------------


    // hier nuzr noch für options menü
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
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
        if(isAdded()) {
            int id = item.getItemId();

            if (id == R.id.equip_save) {
                // Write a message to the database
                saveAndQuit();
            }

            switch (item.getItemId()) {
                case android.R.id.home:
                    getActivity().onBackPressed();
                    return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }


}
