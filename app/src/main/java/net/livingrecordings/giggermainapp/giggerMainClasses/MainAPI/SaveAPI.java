package net.livingrecordings.giggermainapp.giggerMainClasses.MainAPI;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import net.livingrecordings.giggermainapp.R;
import net.livingrecordings.giggermainapp.giggerMainClasses.Exceptions.Gigger_NoCurrentUserException;
import net.livingrecordings.giggermainapp.giggerMainClasses.Exceptions.Gigger_NoItemInputException;
import net.livingrecordings.giggermainapp.giggerMainClasses.models.UploadImgTaskData;
import net.livingrecordings.giggermainapp.giggerMainClasses.helperClasses.LoadImageCasheHelper;
import net.livingrecordings.giggermainapp.giggerMainClasses.jobs.GiggerPicUploadJob;
import net.livingrecordings.giggermainapp.giggerMainClasses.models.BandClass;
import net.livingrecordings.giggermainapp.giggerMainClasses.models.GiggerRootClass;
import net.livingrecordings.giggermainapp.giggerMainClasses.models.ImagesClass;
import net.livingrecordings.giggermainapp.giggerMainClasses.models.ItemClass;
import net.livingrecordings.giggermainapp.giggerMainClasses.models.ItemClassLocal;
import net.livingrecordings.giggermainapp.giggerMainClasses.models.UserClass;

import java.util.HashMap;

/**
 * Created by Kraetzig Neu on 20.01.2017.
 */

public class SaveAPI extends GiggerMainAPI {

        public static final String MAINAPI_SAVE_ERROR = "GIGGER_SAVEI_ERROR";

        public SaveAPI(){
        }

        public static SaveAPI getInstance(){
            return new SaveAPI();
        }

        public void saveProfile(final Context mContext, UserClass inpUser, String inpKey) {
            try {
                super.save(inpUser);
                if (inpUser == null) {
                    // error kein user
                    throw new Gigger_NoItemInputException();
                }
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user == null) {
                    // error kein user
                    throw new Gigger_NoCurrentUserException();
                }
                DatabaseReference ref = pushToRef(getUsersRef(), inpKey);
                getUsersRef(ref.getKey()).setValue(inpUser);

                // bilder hochladen...
                mergeImages(mContext, inpUser, getUserImagesQuery(inpKey), ref.getKey(), "userReference");

                // noch einen toast, das die Aufgabe eingetragen wurde und der upload beginnt.
                Toast.makeText(mContext, mContext.getString(R.string.user_profile_Save), Toast.LENGTH_LONG).show();

            } catch (Exception e) {
                Log.e(MAINAPI_SAVE_ERROR, "(saveProfile)" + e.getMessage());
            }
        }

        public void saveBand(final Context mContext, BandClass inpBand, String inpKey) {
            try {
                super.save(inpBand);
                if (inpBand == null) {
                    // error kein user
                    throw new Gigger_NoItemInputException();
                }
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user == null) {
                    // error kein user
                    throw new Gigger_NoCurrentUserException();
                }
                DatabaseReference ref = pushToRef(getBandReference(), inpKey);
                inpBand.setFounder(user.getUid());

                getLocalItemRef(ref.getKey()).setValue(inpBand);
                // nach dem item setzen müssen noch die ganzen verknüpfungen gesetzt werden.
                // z.b. wird die band dem account hinzugefügt.
                getUsersRef().child(BANDPATH).child(inpKey).setValue(true);

                // bilder hochladen...
                mergeImages(mContext, inpBand, getBandsImageQuery(inpKey), ref.getKey(), "bandsReference");

                // noch einen toast, das die Aufgabe eingetragen wurde und der upload beginnt.
                Toast.makeText(mContext, mContext.getString(R.string.band_entry_success), Toast.LENGTH_LONG).show();

            } catch (Exception e) {
                Log.e(MAINAPI_SAVE_ERROR, "(saveBand)" + e.getMessage());
            }
        }

        public void saveItem(final Context mContext, ItemClass inpItem, final ItemClassLocal itemLocal, String key) {
            class saveItemEssential {
                public saveItemEssential(ItemClass item, String key) {
                    if (item.isPublished()) {
                        getPublishedItemsRef(key).setValue(item);
                        getPrivateItemRef(key).setValue(item);
                    } else {
                        getPublishedItemsRef(key).removeValue();
                        getPrivateItemRef(key).setValue(item);
                    }
                }
            }
            // save it.
            try {
                super.save(inpItem);
                String inpKey = null;
                if (key != null){
                    if(!key.trim().isEmpty()){
                        inpKey = key;
                    }
                }
                if (inpKey == null) {
                    inpItem.setCreatedBy(mUser.getUid());
                }
                DatabaseReference itemRef = pushToRef(getPrivateItemRef(), inpKey);
                inpKey = itemRef.getKey();
                inpItem.setSearchName(inpItem.getName().toUpperCase());

                // dublicate check and forbiddenchars
                HashMap<String, Boolean> map = new HashMap<>();
                for (String s : inpItem.getTags().keySet()) {
                    String afterCheck = removeForbiddenChars(s);
                    if (afterCheck.length() > 0) {
                        map.put(afterCheck, true);
                    }
                }
                inpItem.setTags(map);
                new saveItemEssential(inpItem, inpKey);
                // noch die lokalen...
                // muss ich recht manuell machen.
                itemLocal.setSearchName(inpItem.getName().toUpperCase());

                itemLocal.setTags(inpItem.getTags());
                // TODO TAG global index .. zum löschen s. obewn.
                for (String s : inpItem.getTags().keySet()) {
                    getTagsPublishedRef().child(s.toUpperCase()).child("name").setValue(s);
                    getTagsPublishedRef().child(s.toUpperCase()).child("searchName").setValue(s.toUpperCase());
                }
                getLocalItemRef(inpKey).setValue(itemLocal);
                //----------------------------------
                // dieser abschnitt vergleicht alle hochgeladenen bilder
                mergeImages(mContext, inpItem, getItemImageQuery(inpKey), inpKey, "itemReference");

                // noch einen toast, das die Aufgabe eingetragen wurde und der upload beginnt.
                Toast.makeText(mContext, mContext.getString(R.string.upload_ongoing_toast), Toast.LENGTH_LONG).show();

            } catch (Exception e) {
                Log.e(MAINAPI_SAVE_ERROR, "(saveItem)" + e.getMessage());
            }
        }

        private DatabaseReference pushToRef(DatabaseReference location, String inpKey) {
            DatabaseReference res;
            if (inpKey == null) {
                res = location.push().getRef();
            } else {
                res = location.child(inpKey);
            }
            return res;
        }

        private void mergeImages(final Context mContext, final GiggerRootClass inpItem, Query imagesByClass, final String inpKey, final String savePath){
            imagesByClass.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (ImagesClass uploadImage  : inpItem.getUploadImages()) {
                        Boolean foundInDatabase = false;
                        // alle hochzuladenen Bilder prüfen, ob sie hochgeladen werden müssen.
                        for (DataSnapshot imgDS : dataSnapshot.getChildren()) {
                            ImagesClass img = imgDS.getValue(ImagesClass.class);
                            if (img.getImgUri().equals(uploadImage.getImgUri())) {
                                foundInDatabase = true; // alles ok.
                            }
                        }
                        // wenn found = true, wurde ein dublikat gefunden. also
                        if (!foundInDatabase) {
                            // nicht gefunden, also hochladen.
                            Uri localUploadFile = LoadImageCasheHelper.getInstance().casheImageFromUri(mContext, inpKey, Uri.parse(uploadImage.getImgUri()));
                            // ist ja schon alles im cashe..
                            mJobManager.getJobManager(mContext).addJobInBackground(new GiggerPicUploadJob(new UploadImgTaskData(
                                    inpItem.getName(),
                                    inpKey,
                                    savePath, // hier wird im imageClass gespeichert, zu wem das bild gehört... z.b. BANDPATH,ITEMPATH - oder USERS_PUBLISHED
                                    localUploadFile, // der link zum bild auf der festplatte
                                    uploadImage))
                            ); // muss dann nach dem hochladen gesetzt werden.
                        }
                    }
                    // deletition routine-...
                    for (DataSnapshot imgDS : dataSnapshot.getChildren()) {
                        ImagesClass dbImage = imgDS.getValue(ImagesClass.class);
                        for (ImagesClass img : inpItem.getDeletitionImages()) { // nur keys
                            if (img.getImgUri().equals(dbImage.getImgUri())) {
                                deleteImageFromItem(imgDS.getKey());
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }

        private void deleteImageFromItem(String imgKey) {
        getImageRef().child(imgKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ImagesClass imgToDelete = dataSnapshot.getValue(ImagesClass.class);
                if (imgToDelete != null) {
                    StorageReference sr = mStorage.getReferenceFromUrl(imgToDelete.getImgUri());
                    if (sr != null)
                        getItemStorageRef().child(sr.getName()).delete();
                }
                dataSnapshot.getRef().removeValue(); // immer auch die zugrundeliegene referenz löschenm-.
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

}
