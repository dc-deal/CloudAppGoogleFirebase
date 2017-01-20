package net.livingrecordings.giggermainapp.giggerMainClasses.MainAPI;

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

import net.livingrecordings.giggermainapp.giggerMainClasses.AppJobManager_ForPicUploads;
import net.livingrecordings.giggermainapp.giggerMainClasses.Exceptions.Gigger_NoCurrentUserException;
import net.livingrecordings.giggermainapp.giggerMainClasses.Exceptions.Gigger_NoItemInputException;
import net.livingrecordings.giggermainapp.giggerMainClasses.jobs.GiggerPicUploadJob;
import net.livingrecordings.giggermainapp.giggerMainClasses.models.GiggerRootClass;
import net.livingrecordings.giggermainapp.giggerMainClasses.models.ImagesClass;
import net.livingrecordings.giggermainapp.giggerMainClasses.models.ItemClass;


/**
 * Created by Kraetzig Neu on 15.12.2016.
 */


// items eintragen
public class GiggerMainAPI {

    //EQUIPMENT..
    public static final String ITEMPATH = "ITEMPATH";
    public static final String ITEMPATH_LOCAL = "ITEMPATH_LOCAL";
    public static final String ITEMPATH_GLOBAL = "ITEMPATH_GLOBAL";
    public static final String ITEMPATH_PRIVATE = "ITEMPATH_PRIVATE";
    public static final String TAGS_PUBLISHED = "TAGS";
    // BANDS & Users
    public static final String BANDPATH = "BANDS";
    public static final String USERS_BANDPROCESSES = "USERS_BANDCONTACTSTATES";
    public static final String USERS_PUBLISHED = "USERS_PUBLISHED";
    // GLOBAL
    public static final String IMAGES = "IMAGES";
    // INDEX
    public static final String INDEXES = "INDEXES";
    public static final String TAGITEMS_LOCAL_ALLTAGS_INDEX = "TAGITEMS_LOCAL_ALLTAGS_INDEX";
    public static final String JOB_MANAGER_UPLOADS = "UploadJobsGiggerAPI";
    // LOGS

    // GLOBALS
    public static final int MAX_GLOBALFILE_SIZE = 1000000; //1200000; // 1.2MP
    // local vars
    FirebaseDatabase mDatabase;
    FirebaseStorage mStorage;
    FirebaseAuth mAuth;
    protected FirebaseUser mUser;
    // Für das save
    GiggerPicUploadJob mNotification;
    AppJobManager_ForPicUploads mJobManager;
    private mainAPICallbacks apiCallbacks;;

    public GiggerMainAPI() {
        // apiCallbacks = (mainAPICallbacks) cont;
        mDatabase = FirebaseDatabase.getInstance();
        mStorage = FirebaseStorage.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
    }

    public static GiggerMainAPI getInstance() {
        return new GiggerMainAPI();
    }

    protected DatabaseReference getPublishedItemsRef() {
        return mDatabase.getReference()
                .child(ITEMPATH).child(ITEMPATH_GLOBAL);
    }

    public DatabaseReference getPublishedItemsRef(String itemKey) {
        return getPublishedItemsRef().child(itemKey);
    }

    protected DatabaseReference getPrivateItemRef() {
        return mDatabase.getReference()
                .child(ITEMPATH).child(ITEMPATH_PRIVATE).child(getCurrentUserUID());
    }

    public DatabaseReference getPrivateItemRef(String itemKey) {
        return getPrivateItemRef().child(itemKey);
    }

    public String getCurrentUserUID() {
        return mAuth.getCurrentUser().getUid();
    }
    public Boolean isAnonymous() {
        return mAuth.getCurrentUser().isAnonymous();
    }

    protected String removeForbiddenChars(String inp) {
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
        getItemImageQuery(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    ImagesClass thisImg = dataSnapshot.getValue(ImagesClass.class);
                    StorageReference ref = mStorage.getReferenceFromUrl(thisImg.getImgUri());
                    if (ref != null){
                        ref.delete();
                    }
                }
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

    protected DatabaseReference getLocalItemRef() {
        return mDatabase.getReference()
                .child(ITEMPATH).child(ITEMPATH_LOCAL).child(mUser.getUid());
    }

    public DatabaseReference getLocalItemRef(String itemKey) {
        return getLocalItemRef().child(itemKey);
    }

    public StorageReference getItemStorageRef() {
        return mStorage.getReference()
                .child(ITEMPATH).child(ITEMPATH_GLOBAL);
    }

    public DatabaseReference getTagsPublishedRef() {
        return mDatabase.getReference().child(TAGS_PUBLISHED);
    }

    public DatabaseReference getImageRef() {
        return mDatabase.getReference().child(IMAGES);
    }

    public Query getItemImageQuery(String ItemKey) {
        return mDatabase.getReference().child(IMAGES).orderByChild("itemReference").equalTo(ItemKey);
    }

    public Query getBandsImageQuery(String ItemKey) {
        return mDatabase.getReference().child(IMAGES).orderByChild("bandsReference").equalTo(ItemKey);
    }

    public Query getUserImagesQuery(String ItemKey) {
        return mDatabase.getReference().child(IMAGES).orderByChild("userReference").equalTo(ItemKey);
    }


    public Query getDublicateNameSearchQuery(String nameStr) {
        return getLocalItemRef()
                .orderByChild("searchName").equalTo(nameStr.toUpperCase());
    }

    public Query getDublicateBAndNameNameSearchQuery(String nameStr) {
        return getBandReference()
                .orderByChild("searchName").equalTo(nameStr.toUpperCase());
    }


    public Query getCreatedByUserQuery() {
        return getPrivateItemRef().orderByChild("createdBy").equalTo(
                getCurrentUserUID()
        );
    }

    public Query getQueryAllBandsOfUser(String uID) {
        return getBandReference().orderByChild(USERS_PUBLISHED).equalTo(uID);
    }
    public Query getQueryAllBandprocessesOfUser(String uID) {
        return getUsersRef(uID).orderByChild(USERS_BANDPROCESSES).equalTo(uID);
    }

    public Query getQueryAllUsersOfBand(String bandKEY) {
        return getUsersRef().orderByChild(BANDPATH+"/"+bandKEY).equalTo(true);
    }

    protected DatabaseReference getUsersRef() {
        return mDatabase.getReference().child(USERS_PUBLISHED);
    }

    public DatabaseReference getUsersRef(String itemKey) {
        return getUsersRef().child(itemKey);
    }


    protected DatabaseReference getBandReference() {
        return mDatabase.getReference()
                .child(BANDPATH);
    }

    public DatabaseReference getBandReference(String itemKey) {
        return getBandReference().child(itemKey);
    }


    protected void save(GiggerRootClass rc) throws Exception{
        if (rc == null) {
            // error kein user
            throw new Gigger_NoItemInputException();
        }
        if (mUser == null) {
            // error kein user
            throw new Gigger_NoCurrentUserException();
        }
    }


    public interface mainAPICallbacks {
        // man kann hierrüber callbacken, ob alles geklappt hat und wann es fertig ist.
        void onMainAPIDone(boolean error);
    }


}
