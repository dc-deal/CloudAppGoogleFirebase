package net.livingrecordings.giggermainapp.giggerMainClasses;

import android.net.Uri;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;

import java.util.Date;

/**
 * Created by Kraetzig Neu on 22.12.2016.
 */


public class UploadImgTaskData {
    // man kann dem uploader so einen task geben un der wird es für die Aufgabe einfügen...
    private Uri fileToUpload;
    private DatabaseReference referenceToWriteTo;
    private StorageReference storageToWriteTo;
    private boolean isGlleryPic;


    public UploadImgTaskData(Uri fileToUpload, DatabaseReference referenceToWriteTo, StorageReference storageToWriteTo, Boolean isGlleryPic) {
        this.fileToUpload = fileToUpload;
        this.referenceToWriteTo = referenceToWriteTo;
        this.storageToWriteTo = storageToWriteTo;
        this.isGlleryPic = isGlleryPic;
    }

    String generateUniqueFileName() {
        String filename = "";
        long millis = System.currentTimeMillis();
        String datetime = new Date().toGMTString();
        datetime = datetime.replace(" ", "");
        datetime = datetime.replace(":", "");
        filename = datetime + "_" + millis;
        return filename;
    }


    public Uri getFileToUpload() {
        return fileToUpload;
    }

    public void setFileToUpload(Uri fileToUpload) {
        this.fileToUpload = fileToUpload;
    }

    public DatabaseReference getReferenceToWriteTo() {
        return referenceToWriteTo;
    }

    public void setReferenceToWriteTo(DatabaseReference referenceToWriteTo) {
        this.referenceToWriteTo = referenceToWriteTo;
    }

    public StorageReference getStorageToWriteTo() {
        return storageToWriteTo;
    }

    public void setStorageToWriteTo(StorageReference storageToWriteTo) {
        this.storageToWriteTo = storageToWriteTo;
    }

    public boolean isGlleryPic() {
        return isGlleryPic;
    }

    public void setGlleryPic(boolean glleryPic) {
        isGlleryPic = glleryPic;
    }
}