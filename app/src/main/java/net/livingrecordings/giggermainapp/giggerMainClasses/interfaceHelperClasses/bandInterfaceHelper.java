package net.livingrecordings.giggermainapp.giggerMainClasses.interfaceHelperClasses;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import net.livingrecordings.giggermainapp.BandEditor.UserList;
import net.livingrecordings.giggermainapp.R;
import net.livingrecordings.giggermainapp.giggerMainClasses.MainAPI.GiggerMainAPI;
import net.livingrecordings.giggermainapp.giggerMainClasses.MainAPI.SaveAPI;
import net.livingrecordings.giggermainapp.giggerMainClasses.helperClasses.GiggerIntentHelperClass;
import net.livingrecordings.giggermainapp.giggerMainClasses.helperClasses.LoadImageCasheHelper;
import net.livingrecordings.giggermainapp.giggerMainClasses.models.BandClass;
import net.livingrecordings.giggermainapp.giggerMainClasses.models.ImagesClass;

import java.util.ArrayList;

/**
 * Created by Franky on 11.12.2016.
 */

public class BandInterfaceHelper extends InterfaceHelperRootClass
        implements LoadImageCasheHelper.loadImageCasheHelperCallbacks {

    TextView inputBandName;
    TextView inputBandStyle;
    ImageView inputGalImage;
    String nameStr;
    String styleStr;
    String LOG_TAG = "BANDILOG";
    String thisBandKey;
    BandClass bandClassModel;
    boolean isNUEntry;
    Activity mContext;
    View mView;
    SaveAPI saveAPI;
    LoadImageCasheHelper mImgCasheHelper;
    Uri actUriInImageButton;
    UserList uList = new UserList();
    ListView inputUserList;
    public InterfaceHelperCallbacks itemInterfaceHelperCallbacks;
    FloatingActionButton fabEdit;

    public BandInterfaceHelper(){
    }

    public static BandInterfaceHelper getInstance(){
        return new BandInterfaceHelper();
    }

    public void provideAllImages_asOnlineUri(ArrayList<ImagesClass> imgList){
        actUriInImageButton = Uri.parse(imgList.get(0).getImgUri()); // liste hat immer etwas.
    }

    public void setupBandInterface(final Activity context, final View view, final String bandKey) {
        try {
            itemInterfaceHelperCallbacks = (InterfaceHelperCallbacks) context;
        } catch (Exception e) {
            Log.e(LOG_TAG, "Interface InterfaceHelperCallbacks is not bound. This may cause trouble... eg. Save routine");
        }
        thisBandKey = bandKey.trim();
        isNUEntry = thisBandKey.isEmpty();
        saveAPI = SaveAPI.getInstance();
        mImgCasheHelper = LoadImageCasheHelper.getInstance();
        mImgCasheHelper.loadImageCallbacks = this;
        mContext = context;
        mView = view;
        if (!isNUEntry) {
            saveAPI.getPrivateItemRef(thisBandKey).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    bandClassModel = dataSnapshot.getValue(BandClass.class);
                    if (bandClassModel != null) {
                        inputBandName.setText(bandClassModel.getName());
                        inputBandStyle.setText(bandClassModel.getStyle());

                        if (inputUserList != null) {
                            uList.startUserList(mContext,inputUserList,dataSnapshot.getKey());
                        }
                        mImgCasheHelper.loadGalleryImage_Cashed(mContext, inputGalImage, bandKey);

                        // bearbeiten button....
                        if (bandClassModel.getFounder().equals(GiggerMainAPI.getInstance().getCurrentUserUID())){
                            // fab aktivieren...
                            if (fabEdit != null){
                                fabEdit.setVisibility(View.VISIBLE);
                                fabEdit.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        GiggerIntentHelperClass.getInstance(mContext).intentEditBand(bandKey);
                                    }
                                });
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
        inputBandName = (TextView) mView.findViewById(R.id.bandEditor_name);
        // listener setzen.
        inputBandStyle = (TextView) mView.findViewById(R.id.bandEditor_Style);
        inputGalImage = (ImageView) mView.findViewById(R.id.bandDetail_image);
        inputUserList = (ListView) mView.findViewById(R.id.bandmembers);

        fabEdit = (FloatingActionButton) mView.findViewById(R.id.fab_editband);
    }

    public String getKey() {
        if (isNUEntry) {
            Log.e(LOG_TAG, "Itemkey demanded with newItem.. this wo't work!!");
        }
        return thisBandKey;
    }


    private void LoadGalleryImage(Uri inp) {
        Picasso.with(mContext)
                .load(inp)
                .placeholder(R.drawable.imgplaceholder)// //R.drawable.progress_animation
                .error(R.drawable.ic_error_outline_black_24dp)
                .into((ImageView) mView.findViewById(R.id.viewItemImg));
    }

    private View myFindViewbyName(String vName) {
        int id = mContext.getResources().getIdentifier(vName, "id", mContext.getPackageName());
        return mView.findViewById(id);
    }

    /// kommt vo mfragment, um ein image zu setten.
    public void putGalleryUri(Uri inp) {
        // view...
        LoadGalleryImage(inp);
        if (actUriInImageButton != null){
            bandClassModel.addDeletePic(actUriInImageButton); // unter umständen das aktuelle beld löschen, falls es nicht null war...
        }
        actUriInImageButton = inp;
    }

    public void saveItemFromInterface() {
        nameStr = inputBandName.getText().toString().trim();
        styleStr = inputBandStyle.getText().toString().trim();
        // prüfungen...
        if ((!nameStr.equals("") && (nameStr.length() > 2))) {
            // prüfung ob der Gegenstand schon da ist,.
            if (isNUEntry) {
                saveAPI.getDublicateBAndNameNameSearchQuery(nameStr.toUpperCase()).addListenerForSingleValueEvent(new ValueEventListener() {
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
            Toast.makeText(mContext, mContext.getString(R.string.band_name_Error), Toast.LENGTH_SHORT).show();
        }
    }

    private Dialog overrideDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(R.string.band_exists)
                .setPositiveButton(R.string.btt_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    private void setAndSave() {
        bandClassModel.setName(nameStr);
        bandClassModel.setStyle(styleStr);
        bandClassModel.addUploadImage(new ImagesClass(true,actUriInImageButton.toString(), 1, "", "", ""));

        saveAPI.saveBand(mContext, this.bandClassModel, thisBandKey);
        if (itemInterfaceHelperCallbacks != null) {
            itemInterfaceHelperCallbacks.onSaveProgressComplete();
        }
    }
}
