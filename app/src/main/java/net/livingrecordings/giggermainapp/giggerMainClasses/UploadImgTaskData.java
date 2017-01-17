package net.livingrecordings.giggermainapp.giggerMainClasses;

import android.net.Uri;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;

import net.livingrecordings.giggermainapp.giggerMainClasses.helperClasses.UriHelper;

import java.util.Date;

/**
 * Created by Kraetzig Neu on 22.12.2016.
 */


public class UploadImgTaskData {
    // man kann dem uploader so einen task geben un der wird es für die Aufgabe einfügen...
    private Uri fileToUpload;
    private boolean isGlleryPic;
    private String nameOfItem;
    private String dbKey;


    public UploadImgTaskData(String Name,String dbKey, Uri fileToUpload, Boolean isGlleryPic) {
        this.dbKey = dbKey;
        this.nameOfItem = Name;
        this.fileToUpload = fileToUpload;
        this.isGlleryPic = isGlleryPic;
    }



    public Uri getFileToUpload() {
        return fileToUpload;
    }

    public void setFileToUpload(Uri fileToUpload) {
        this.fileToUpload = fileToUpload;
    }

    public boolean isGlleryPic() {
        return isGlleryPic;
    }

    public void setGlleryPic(boolean glleryPic) {
        isGlleryPic = glleryPic;
    }

    public String getNameOfItem() {
        return nameOfItem;
    }

    public void setNameOfItem(String nameOfItem) {
        this.nameOfItem = nameOfItem;
    }

    public String getDbKey() {
        return dbKey;
    }

    public void setDbKey(String dbKey) {
        this.dbKey = dbKey;
    }
}