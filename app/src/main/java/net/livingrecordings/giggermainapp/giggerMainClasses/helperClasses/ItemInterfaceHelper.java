package net.livingrecordings.giggermainapp.giggerMainClasses.helperClasses;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import net.livingrecordings.giggermainapp.giggerMainClasses.GiggerItemAPI;
import net.livingrecordings.giggermainapp.giggerMainClasses.InterfaceObjects.TokenAutocompleteEdit;
import net.livingrecordings.giggermainapp.giggerMainClasses.models.ImagesClass;
import net.livingrecordings.giggermainapp.giggerMainClasses.models.ItemClass;
import net.livingrecordings.giggermainapp.giggerMainClasses.models.ItemClassLocal;

import net.livingrecordings.giggermainapp.R;

import java.util.ArrayList;

/**
 * Created by Franky on 11.12.2016.
 */

public class ItemInterfaceHelper implements LoadImageCasheHelper.loadImageCasheHelperCallbacks {
    public InterfaceHelperCallbacks callbacks;
    TextView inputName;
    TextView inputDesc;
    TokenAutocompleteEdit inputTags;
    ImageButton inputGalImage;
    CheckBox inputPublishedCB;
    TextView inputPublishedTextView;
    String nameStr;
    String descStr;
    String LOG_TAG = "INTHELP";
    String thisItemKey;
    ItemClass itemClassModel;
    boolean isNUEntry;
    Activity mContext;
    View mView;
    GiggerItemAPI mainAPI;
    LoadImageCasheHelper mImgCasheHelper;
    Uri actUriInImageButton;

    public void provideAllImages_asOnlineUri(ArrayList<ImagesClass> imgList){
        actUriInImageButton = Uri.parse(imgList.get(0).getImgUri()); // liste hat immer etwas.
    }

    public ItemClass getCurrentItem(){
        return itemClassModel;
    }

    public ItemInterfaceHelper(final Activity context, final View view, final String itemKey) {
        try {
            callbacks = (InterfaceHelperCallbacks) context;
        } catch (Exception e) {
            Log.e("INTHELP", "Interface InterfaceHelperCallbacks is not bound. This may cause trouble... eg. Save routine");
        }
        thisItemKey = itemKey.trim();
        isNUEntry = thisItemKey.isEmpty();
        itemClassModel = new ItemClass();
        mainAPI = GiggerItemAPI.getInstance();
        mImgCasheHelper = LoadImageCasheHelper.getInstance();
        mImgCasheHelper.callbacks = this;
        // methode nimmt die view und füllt
        mContext = context;
        mView = view;
        if (!isNUEntry) {
            // kann nur referenzieren wenn es kein neueintrag ist
            mainAPI.getPrivateItemRef(thisItemKey).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    itemClassModel = dataSnapshot.getValue(ItemClass.class);
                    if (itemClassModel != null) {
                        itemClassModel.setDbKey(itemKey);
                        inputName.setText(itemClassModel.getName());
                        inputDesc.setText(itemClassModel.getDesc());
                        inputTags.setInputSet(itemClassModel.getTags().keySet());
                        publishedCheck();
                        // titel....
                        mContext.setTitle(mContext.getResources()
                                .getString(R.string.title_EquipEditorActivity_ItemDetail) + " " + itemClassModel.getName());
                        // abfrage auf die DB ob das item ein galleriebild hat.
                        mImgCasheHelper.loadGalleryImage_Cashed(mContext, inputGalImage, thisItemKey);
                        // dem aktuellen gegenstand noch alle images heinzufügen,
                        // damit die speicher routine sie nicht löscht!!
                    //    loadImagesIntoView();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
        inputName = (TextView) mView.findViewById(R.id.viewItemName);
        // listener setzen.
        inputDesc = (TextView) mView.findViewById(R.id.viewItemDesc);
        inputTags = (TokenAutocompleteEdit) mView.findViewById(R.id.viewItemTags);
        inputGalImage = (ImageButton) mView.findViewById(R.id.viewItemImg);
        View v = mView.findViewById(R.id.viewItemPublishedCB);
        if (v.getClass().equals(AppCompatCheckBox.class)) {
            inputPublishedCB = (CheckBox) mView.findViewById(R.id.viewItemPublishedCB);
        }
        if (v.getClass().equals(AppCompatTextView.class)) {
            inputPublishedTextView = (TextView) mView.findViewById(R.id.viewItemPublishedCB);
        }
    }

    public String getKey() {
        if (isNUEntry) {
            Log.e("INTHELP", "Itemkey demanded with newItem.. this wo't work!!");
        }
        return thisItemKey;
    }

    private void publishedCheck() {
        if (inputPublishedCB != null) {
            inputPublishedCB.setChecked(itemClassModel.isPublished());
        }
        if (inputPublishedTextView != null) {
            if (itemClassModel.isPublished())
              inputPublishedTextView.setVisibility(View.VISIBLE);
        }
    }

    private void LoadGalleryImage(Uri inp) {
        Picasso.with(mContext)
                .load(inp)
                .placeholder(R.drawable.imgplaceholder)// //R.drawable.progress_animation
                .error(R.drawable.erroricon)
                .into((ImageView) mView.findViewById(R.id.viewItemImg));
    }

    private View myFindViewbyName(String vName) {
        int id = mContext.getResources().getIdentifier(vName, "id", mContext.getPackageName());
        return mView.findViewById(id);
    }

    private void loadImagesIntoView() {
        LinearLayout layout = (LinearLayout) myFindViewbyName("viewImgScrollLinear");
       /*
       // hierm uss ich meine eigene Komponente machen, die für die scroll view sorgt.
       // TODO sie wird dann mehrere bilder anzeigen könenen.

       for (String s : itemClassModel.getImgs().values()) {
            if (s != itemClassModel.getGalleryPic()) {
                // natürlich nciht das galleriebild laden...
                ImageView imageView = new ImageView(mContext);
                imageView.setPadding(2, 2, 2, 2);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                insertedImgViews.add(imageView);
                layout.addView(imageView);
                LoadImageCashed(imageView, s);
            }
        }*/
    }

    /// kommt vo mfragment, um ein image zu setten.
    public void putGalleryUri(Uri inp) {
        // view...
        LoadGalleryImage(inp);
        if (actUriInImageButton != null){
            itemClassModel.addDeletePic(actUriInImageButton); // unter umständen das aktuelle beld löschen, falls es nicht null war...
        }
        actUriInImageButton = inp;
    }

    public void saveItemFromInterface() {
        nameStr = inputName.getText().toString().trim();
        descStr = inputDesc.getText().toString().trim();
        // prüfungen...
        if ((!nameStr.equals("") && (nameStr.length() > 2))) {
            // prüfung ob der Gegenstand schon da ist,.
            if (isNUEntry) {
                mainAPI.getDublicateNameSearchQuery(nameStr.toUpperCase()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() == null) {
                            setAndSave();
                        } else {
                            overrideDialog().show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            } else {
                setAndSave();
            }

        } else {
            Toast.makeText(mContext, mContext.getString(R.string.item_name_error), Toast.LENGTH_SHORT).show();
        }
    }

    private Dialog overrideDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(R.string.item_already_exists)
                .setPositiveButton(R.string.btt_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        setAndSave();
                    }
                })
                .setNegativeButton(R.string.btt_abort, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    private void setAndSave() {
        itemClassModel.setName(nameStr);
        itemClassModel.setDesc(descStr);
        itemClassModel.setTags(inputTags.getAsHashMap());
        itemClassModel.setPublished(inputPublishedCB.isChecked());
        itemClassModel.addUploadImage(new ImagesClass(true,actUriInImageButton.toString(),1));

        mainAPI.saveItem(mContext, this.itemClassModel, new ItemClassLocal());
        if (callbacks != null) {
            callbacks.onSaveProgressComplete();
        }
    }

    public interface InterfaceHelperCallbacks {
        void onSaveProgressComplete();
    }
}
