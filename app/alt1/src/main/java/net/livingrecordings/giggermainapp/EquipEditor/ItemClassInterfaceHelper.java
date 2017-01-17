package net.livingrecordings.giggermainapp.EquipEditor;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
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

import net.livingrecordings.giggermainapp.EquipEditor.CategoryDlg;
import net.livingrecordings.giggermainapp.R;
import net.livingrecordings.giggermainapp.giggerMainClasses.GiggerContactCollection;
import net.livingrecordings.giggermainapp.giggerMainClasses.GiggerMainAPI;
import net.livingrecordings.giggermainapp.giggerMainClasses.ItemClass;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by Franky on 11.12.2016.
 */

public class ItemClassInterfaceHelper
implements CategoryDlg.categoryDLGEvents {
    OnPausedListener mProgressPause = new OnPausedListener<UploadTask.TaskSnapshot>() {
        @Override
        public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
            System.out.println("Upload is paused");
        }
    };
    private Uri file;
    private String thisItemKey,inputCat,outputCat;
    private Boolean fileChanged;
    private ItemClass thisItem;
    private boolean isNUEntry;
    // own stuff.. put image
    private ArrayList<ImageView> insertedImgViews;
    private ArrayList<Uri> insertedImgUris;
    private Context mContext;
    private View mView;
    private DatabaseReference thisRef;
    private StorageReference thisStorageRef;
    private ProgressDialog mProgress;
    private GiggerMainAPI mainAPI;
    OnProgressListener mProgressProgListener = new OnProgressListener<UploadTask.TaskSnapshot>() {
        @Override
        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
            System.out.println("Upload is " + progress + "% done");
            int currentprogress = (int) progress;
            mProgress.setProgress(currentprogress);
        }
    };

    public ItemClassInterfaceHelper(final Context context, final View view, String itemKey, String inpCat) {
        inputCat = inpCat;
        thisItemKey = itemKey.trim();
        isNUEntry = thisItemKey.isEmpty();
        thisItem = new ItemClass();
        mainAPI = new GiggerMainAPI(mContext);
        // methode nimmt die view und füllt
        mContext = context;
        mView = view;
        thisRef = FirebaseDatabase.getInstance()
                .getReference("userItemsData")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        thisStorageRef = FirebaseStorage.getInstance()
                .getReference("userItemsData")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        if (!isNUEntry) {
            // kann nur referenzieren wenn es kein neueintrag ist
            thisRef.child(thisItemKey);
            thisStorageRef.child(thisItemKey);
        }
        mProgress = new ProgressDialog(mContext);
        mProgress.setMessage(context.getString(R.string.save));
        insertedImgUris = new ArrayList<>();
        insertedImgViews = new ArrayList<>();
        thisRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                thisRef.removeEventListener(this); // Wichtig rekursionen vermeiden!
                // setter
                outputCat = inputCat;
                updateParCat(thisRef.getParent().child(outputCat));
                if (!isNUEntry) {
                    thisItem.setName((String) dataSnapshot.child("name").getValue());
                    thisItem.setDesc((String) dataSnapshot.child("desc").getValue());
      //              thisItem.setImg((String) dataSnapshot.child("img").getValue());
                    thisItem.setParCat((String) dataSnapshot.child("parCat").getValue());
                    updateParCat(thisRef.getParent().child(thisItem.getParCat()));
                }
                // interface update...
                setViewByName("viewItemName", thisItem.getName(), false);
                setViewByName("viewItemDesc", thisItem.getDesc(), false);
                // listend zu der parent kacegory für den namen.
                Uri ur = null;
        //        if (thisItem.getImg() != null)
       //           ur = Uri.parse(thisItem.getImg());
                setViewByName("viewItemImg", ur, false);
                // bilder einladen...
                Iterable<DataSnapshot> idsn = dataSnapshot.child("imgAdded").getChildren();
                for (DataSnapshot ds : idsn) {
                    insertedImgUris.add(Uri.parse((String) ds.child("img").getValue()));
                }
                loadImagesIntoView();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    void updateParCat(DatabaseReference parCatRef){
        thisItem.setParCat(parCatRef.getKey());
        parCatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                setViewByName("viewItemParCat", (String) dataSnapshot.child("name").getValue(), false);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    @Override
    public void onChooseCategory(String chooseKey){
        if (chooseKey != null) {
            updateParCat(thisRef.getParent().child(chooseKey));
        }
    }

    private void LoadGalleryImage(Uri inp, ImageView v) {
        Picasso.with(mContext)
                .load(inp)
                .placeholder(R.drawable.imgplaceholder)// //R.drawable.progress_animation
                .error(R.drawable.erroricon)
                .into(v);
    }

    private View myFindViewbyName(String vName) {
        int id = mContext.getResources().getIdentifier(vName, "id", mContext.getPackageName());
        return mView.findViewById(id);
    }

    private Object setViewByName(String vName, Object putObjInto, Boolean getVal) {
        Object res = null;
        View foundV = myFindViewbyName(vName);
        if (putObjInto != null) {
            // wenn es null ist ist es ja nicht wichtig...
            if (putObjInto.equals(String.class)) {
                // okay ein string kann es nur noch textview oder edittext sein...
                // reicht schon edit text erbt von textview
                String inp = (String) putObjInto;
                if (foundV.equals(TextView.class)) {
                    // OK text einfügen...
                    if (getVal) {
                        res = new String((String) ((TextView) foundV).getText());
                        TextView tv = (TextView) foundV;
                    } else {
                        ((TextView) foundV).setText(inp);
                    }

                }
            }
            if (putObjInto.equals(Uri.class)) {
                // ein Image... wenn es uri ist wird imme ein image angesprochen..
                // show galleryImage!
                if (foundV.equals(ImageView.class)) {
                    LoadGalleryImage((Uri) putObjInto, (ImageView) foundV);
                }
            }
        }
        return res;
    }

    private void loadImagesIntoView() {
        LinearLayout layout = (LinearLayout) myFindViewbyName("viewImgScrollLinear");
        for (int i = 0; i < insertedImgUris.size(); i++) {
            ImageView imageView = new ImageView(mContext);
            imageView.setId(i);
            imageView.setPadding(2, 2, 2, 2);
            // TODO COntroller machen!?!?
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            insertedImgViews.add(imageView);
            layout.addView(imageView);
            LoadGalleryImage(insertedImgUris.get(i), imageView);
        }
    }

    /// put image and show in view....
    public void putGalleryUri(Uri inp) {
        // view...
        setViewByName("viewItemImg", inp, false);
        file = inp;
        fileChanged = true;
    }

    public void saveItem() {
        mainAPI.saveItemService(this.thisItem);
    }

//    private File createImageFile() throws IOException {
//        // Create an image file name
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        String imageFileName = "JPEG_" + timeStamp + "_";
//        File storageDir = mContext.getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//        File image = File.createTempFile(
//                imageFileName,  /* prefix */
//                ".jpg",         /* suffix */
//                storageDir      /* directory */
//        );
//
//        // Save a file: path for use with ACTION_VIEW intents
//     //   mCurrentPhotoPath = "file:" + image.getAbsolutePath();
//        return image;
//    }

}
