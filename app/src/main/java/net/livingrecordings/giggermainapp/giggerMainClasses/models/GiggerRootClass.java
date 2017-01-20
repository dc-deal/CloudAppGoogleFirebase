package net.livingrecordings.giggermainapp.giggerMainClasses.models;

import android.net.Uri;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Kraetzig Neu on 20.01.2017.
 * Klasse gibt ein basis für alle Medels, die auch Bildupload integriert haben.
 * Der ImageCasheHelper und die API's können so die input images einlesen..
 */

@SuppressWarnings("serial") //With this annotation we are going to hide compiler warnings
@IgnoreExtraProperties
public class GiggerRootClass implements Serializable{

    private String name;

    public GiggerRootClass(){

    }

    public GiggerRootClass(String name){
        this.name = name;
    }

    // dann wird sie so abgespreichertn. vorteil: bilder die shcon da sind werden nicht neu hochgeladen.
    // extras...
    @Exclude
    private ArrayList<ImagesClass> uploadImages = new ArrayList<>();
    @Exclude
    private ArrayList<ImagesClass> deleteList = new ArrayList<>();

    @Exclude
    public Boolean addUploadImage(ImagesClass upImg){
        Boolean added = false;
        Boolean equalFound = false;
        for (ImagesClass img : uploadImages){
            if (img.getImgUri().equals(upImg.getImgUri())){
                equalFound = true;
            }
        }
        if (!equalFound){
            uploadImages.add(upImg);
            return true;
        }
        return added;
    }
    @Exclude
    public void addDeletePic(Uri upImg){
        deleteList.add(new ImagesClass(false,upImg.toString(), 0, "", "", "")); // das image Objekt ist hier wohl eher ein dummy.
    }

    @Exclude
    public void addDeletitionPicture(Uri inpUrl) {
        this.deleteList.add(new ImagesClass(false,inpUrl.toString(), 0, "", "", ""));
    }
    @Exclude
    public ArrayList<ImagesClass> getUploadImages(){
        return uploadImages;
    }
    @Exclude
    public ArrayList<ImagesClass> getDeletitionImages(){
        return deleteList;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
