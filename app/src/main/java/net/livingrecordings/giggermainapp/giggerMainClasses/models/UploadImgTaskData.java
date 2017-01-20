package net.livingrecordings.giggermainapp.giggerMainClasses.models;

import android.net.Uri;

import net.livingrecordings.giggermainapp.giggerMainClasses.models.ImagesClass;

/**
 * Created by Kraetzig Neu on 22.12.2016.
 */


public class UploadImgTaskData {
    // man kann dem uploader so einen task geben un der wird es für die Aufgabe einfügen...
    private Uri fileToUpload;
    private ImagesClass ImageC;
    private String nameOfItem;
    private String dbKey,savePath;


    public UploadImgTaskData(String Name,String dbKey,String savePath, Uri fileToUpload, ImagesClass imageC) {
        this.dbKey = dbKey;
        this.nameOfItem = Name;
        this.fileToUpload = fileToUpload;
        this.savePath = savePath; // z.b. BANDS  oder IMAGES_LOCAL
        this.ImageC = imageC; // ist es ein galleriebeild, ev .auch reihenfolgen...
    }



    public Uri getFileToUpload() {
        return fileToUpload;
    }

    public void setFileToUpload(Uri fileToUpload) {
        this.fileToUpload = fileToUpload;
    }

    public ImagesClass isImageC() {
        return ImageC;
    }

    public void setImageC(ImagesClass imageC) {
        ImageC = imageC;
    }

    public String getNameOfItem() {
        return nameOfItem;
    }

    public void setNameOfItem(String nameOfItem) {
        this.nameOfItem = nameOfItem;
    }

    public String getItemKey() {
        return dbKey;
    }

    public void setDbKey(String dbKey) {
        this.dbKey = dbKey;
    }

    public String getSavePath() {
        return savePath;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }
}