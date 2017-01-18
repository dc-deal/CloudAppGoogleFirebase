package net.livingrecordings.giggermainapp.giggerMainClasses;

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
    private String dbKey;


    public UploadImgTaskData(String Name,String dbKey, Uri fileToUpload, ImagesClass imageC) {
        this.dbKey = dbKey;
        this.nameOfItem = Name;
        this.fileToUpload = fileToUpload;
        this.ImageC = imageC;
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
}