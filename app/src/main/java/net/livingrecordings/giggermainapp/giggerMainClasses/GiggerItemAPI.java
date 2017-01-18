package net.livingrecordings.giggermainapp.giggerMainClasses;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import net.livingrecordings.giggermainapp.R;
import net.livingrecordings.giggermainapp.giggerMainClasses.Exceptions.Gigger_NoCurrentUserException;
import net.livingrecordings.giggermainapp.giggerMainClasses.Exceptions.Gigger_NoItemInputException;
import net.livingrecordings.giggermainapp.giggerMainClasses.helperClasses.LoadImageCasheHelper;
import net.livingrecordings.giggermainapp.giggerMainClasses.jobs.GiggerPicUploadJob;
import net.livingrecordings.giggermainapp.giggerMainClasses.models.ImagesClass;
import net.livingrecordings.giggermainapp.giggerMainClasses.models.ItemClass;
import net.livingrecordings.giggermainapp.giggerMainClasses.models.ItemClassLocal;

import java.util.HashMap;


/**
 * Created by Kraetzig Neu on 15.12.2016.
 */


// items eintragen
public class GiggerItemAPI {

    //PFADE..
    public static final String ITEMPATH = "ITEMPATH";
    public static final String ITEMPATH_LOCAL = "ITEMPATH_LOCAL";
    public static final String ITEMPATH_GLOBAL = "ITEMPATH_GLOBAL";
    public static final String ITEMPATH_PRIVATE = "ITEMPATH_PRIVATE";
    public static final String ITEMPATH_IMAGES = "IMAGES";
    public static final String TAGS_PUBLISHED = "TAGS";
    public static final String BANDS_PUBLISHED = "BANDS_PUBLISHED";
    public static final String USERS_PUBLISHED = "USERS_PUBLISHED";
    // INDEX
    public static final String INDEXES = "INDEXES";
    public static final String TAGITEMS_LOCAL_ALLTAGS_INDEX = "TAGITEMS_LOCAL_ALLTAGS_INDEX";
    public static final String BANDS_GLOBAL_NAMESEARCH_INDEX = "BANDS_GLOBAL_NAMESEARCH_INDEX";
    public static final String JOB_MANAGER_UPLOADS = "UploadJobsGiggerAPI";
    // LOGS
    public static final String GIGGERMAINAPI_SAVE_ERROR = "GIGGER_SAVEI_ERROR";
    // GLOBALS
    public static final int MAX_GLOBALFILE_SIZE = 1000000; //1200000; // 1.2MP
    // local vars
    FirebaseDatabase mDatabase;
    FirebaseStorage mStorage;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    // Für das save
    Boolean isNewEntry;
    GiggerPicUploadJob mNotification;
    AppJobManager_ForPicUploads mJobManager;
    private mainAPICallbacks apiCallbacks;
    public GiggerItemAPI() {
        // apiCallbacks = (mainAPICallbacks) cont;
        mDatabase = FirebaseDatabase.getInstance();
        mStorage = FirebaseStorage.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
    }

    public static GiggerItemAPI getInstance() {
        return new GiggerItemAPI();
    }

    private DatabaseReference getPublishedItemsRef() {
        return mDatabase.getReference()
                .child(ITEMPATH).child(ITEMPATH_GLOBAL);
    }

    public DatabaseReference getPublishedItemsRef(String itemKey) {
        return getPublishedItemsRef().child(itemKey);
    }

    private DatabaseReference getPrivateItemRef() {
        return mDatabase.getReference()
                .child(ITEMPATH).child(ITEMPATH_PRIVATE).child(getCurrentUser().getUid());
    }

    public DatabaseReference getPrivateItemRef(String itemKey) {
        return getPrivateItemRef().child(itemKey);
    }

    public FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }

    private String removeForbiddenChars(String inp) {
        // chekck if chars correct..
        // check if dublicate..
        String posString = "";
        final char[] ReservedChars = {'/', '.', '#', '$', '[', ']'};
        Boolean found;
        for (char c : inp.toCharArray()) {
            found = false;
            for (char ckChar : ReservedChars) {
                if (ckChar == c) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                posString = posString + c;
            }
        }
        return posString;
    }

    public void removeItem(final String key) {
        getPublishedItemsRef().child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ItemClass thisItem = dataSnapshot.getValue(ItemClass.class);
                // tags löschen?? aber wie .. wenn sie doch global genutzt werden...
                // INDEXE!!
                getItemStorageRef(key).delete();
                // remove all storage items!!
                getPrivateItemRef(key).removeValue();
                getPublishedItemsRef(key).removeValue();
                getLocalItemRef(key).removeValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private DatabaseReference getLocalItemRef() {
        return mDatabase.getReference()
                .child(ITEMPATH).child(ITEMPATH_LOCAL).child(mUser.getUid());
    }

    public DatabaseReference getLocalItemRef(String itemKey) {
        return getLocalItemRef().child(itemKey);
    }

    public StorageReference getItemStorageRef(String itemKey) {
        return mStorage.getReference()
                .child(ITEMPATH).child(ITEMPATH_GLOBAL).child(itemKey);
    }

    public DatabaseReference getTagsPublishedRef() {
        return mDatabase.getReference().child(TAGS_PUBLISHED);
    }

    public DatabaseReference getImagesRef(String itemKey){
        return mDatabase.getReference()
                .child(ITEMPATH).child(ITEMPATH_IMAGES).child(itemKey);
    }
    public Query getGalleryImageQuery(String ItemKey) {
        return getImagesRef(ItemKey).orderByChild("gallery").equalTo(true);
    }

    public Query getDublicateNameSearchQuery(String nameStr) {
        return getLocalItemRef()
                .orderByChild("searchName").equalTo(nameStr.toUpperCase());
    }

    public Query getCreatedByUserQuery() {
        return getPrivateItemRef().orderByChild("createdBy").equalTo(
                getCurrentUser().getUid()
        );
    }

    private void deleteImageFromItem(final String itemKey, String imgKey) {
        getImagesRef(itemKey).child(imgKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ImagesClass imgToDelete = dataSnapshot.getValue(ImagesClass.class);
                if (imgToDelete != null) {
                    StorageReference sr = mStorage.getReferenceFromUrl(imgToDelete.getImgUri());
                    if (sr != null)
                        getItemStorageRef(itemKey).child(sr.getName()).delete();
                }
                dataSnapshot.getRef().removeValue(); // immer auch die zugrundeliegene referenz löschenm-.
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
/*
    private ArrayList<String> getUploadPics(ItemClass refItem, ItemClass inpItem) {
        ArrayList<String> uniquevalues = new ArrayList(inpItem.getImgs().values());
        uniquevalues.removeAll(refItem.getImgs().values()); // retainAll .. alle behalten..dublikate finden.
        // gallery.... hinzufügen ()
        if (!uniquevalues.contains(inpItem.getGalleryPic()) &&
                (inpItem.getGalleryPic() != null)) {
            uniquevalues.add(inpItem.getGalleryPic());
        }
        return uniquevalues;
    }*/

/*    private ItemClass mergeImage(ItemClass inpItem, String image, Boolean isgallery) {
        HashMap<String, String> hm = inpItem.getImgs();
        if (!inpItem.getImgs().containsValue(image)) {
            hm.put(LoadImageCasheHelper.getInstance().generateUniqueIdentifier(), inpItem.getGalleryPic());
        }
        if (isgallery) {
            inpItem.setGalleryPic(image);
        }
        inpItem.setImgs(hm);
        return inpItem;
    }*/

    public void saveItem(final Context mContext, ItemClass item, final ItemClassLocal itemLocal) {
        class saveItemEssential {
            public saveItemEssential(ItemClass item, String key){
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
            final ItemClass inpItem = item;
            if (inpItem == null) {
                // error kein user
                throw new Gigger_NoItemInputException();
            }
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user == null) {
                // error kein user
                throw new Gigger_NoCurrentUserException();
            }

            inpItem.setCreatedBy(user.getUid());
            isNewEntry = false;
            if (inpItem.getDbKey() == null) {
                inpItem.setDbKey(getPublishedItemsRef().push().getKey());
                isNewEntry = true;

            }
            final String key = inpItem.getDbKey();

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
            new saveItemEssential(inpItem, key);
            // noch die lokalen...
            // muss ich recht manuell machen.
            itemLocal.setSearchName(inpItem.getName().toUpperCase());

            itemLocal.setTags(inpItem.getTags());
            // TODO TAG global index .. zum löschen s. obewn.
            for (String s : inpItem.getTags().keySet()) {
                getTagsPublishedRef().child(s.toUpperCase()).child("name").setValue(s);
                getTagsPublishedRef().child(s.toUpperCase()).child("searchName").setValue(s.toUpperCase());
            }
            getLocalItemRef(key).setValue(itemLocal);
            //----------------------------------
            // dieser abschnitt vergleicht alle hochgeladenen bilder
            mergeImages(mContext,inpItem);

            // noch einen toast, das die Aufgabe eingetragen wurde und der upload beginnt.
            Toast.makeText(mContext, mContext.getString(R.string.upload_ongoing_toast), Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            Log.e(GIGGERMAINAPI_SAVE_ERROR, e.getMessage());
        }
    }


    private void mergeImages(final Context mContext, final ItemClass inpItem){
        final String key = inpItem.getDbKey();
        getImagesRef(key).addListenerForSingleValueEvent(new ValueEventListener() {
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
                        Uri localUploadFile = LoadImageCasheHelper.getInstance().casheImageFromUri(mContext, inpItem.getDbKey(), Uri.parse(uploadImage.getImgUri()));
                        // ist ja schon alles im cashe..
                        mJobManager.getJobManager(mContext).addJobInBackground(new GiggerPicUploadJob(new UploadImgTaskData(
                                inpItem.getName(),
                                key,
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
                            deleteImageFromItem(key, imgDS.getKey());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public interface mainAPICallbacks {
        // man kann hierrüber callbacken, ob alles geklappt hat und wann es fertig ist.
        void onMainAPIDone(boolean error);
    }


}
