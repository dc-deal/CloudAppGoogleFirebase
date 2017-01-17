package net.livingrecordings.giggermainapp.giggerMainClasses;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.StringBuilderPrinter;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
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

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

/**
 * Created by Franky on 11.12.2016.
 */
@SuppressWarnings("serial") //With this annotation we are going to hide compiler warnings
public class ItemClass implements Serializable {

    private String name, desc, parCat,createdBy;
    private HashMap<String,String> imgs = new HashMap();
    private HashMap<String,String> tags = new HashMap();
    // helper...
    private String dbKey;
    public ArrayList<Uri> newImagesInput = new ArrayList<>(); // die reihenfolge der Bilder, wie sie neun in dem Objekt hinterlegt werdne kann
    // dann wird sie so abgespreichertn. vorteil: bilder die shcon da sind werden nicht neu hochgeladen.

    public ItemClass() {
    }

    // LIGHTWEIGHT FIREBASE CONSTRUCTOR...
    // NO FUNCTIONALITY HERE!!!
    public ItemClass(String name, String desc, String parCat, HashMap<String, String> imgs, HashMap<String, String> tags, String createdBy) {
        this.name = name;
        this.desc = desc;
        this.parCat = parCat;
        this.imgs = imgs;
        this.createdBy = createdBy;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getParCat() {
        return parCat;
    }

    public void setParCat(String parCat) {
        this.parCat = parCat;
    }

    public String getDbKey() {
        return dbKey;
    }

    public void setDbKey(String dbKey) {
        this.dbKey = dbKey;
    }

    public HashMap<String, String> getImgs() {
        return imgs;
    }

    public void setImgs(HashMap<String, String> imgs) {
        this.imgs = imgs;
    }

    public HashMap<String, String> getTags() {
        return tags;
    }

    public void setTags(HashMap<String, String> tags) {
        this.tags = tags;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
}
