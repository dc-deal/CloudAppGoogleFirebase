package net.livingrecordings.giggermainapp.giggerMainClasses.models;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

/**
 * Created by Kraetzig Neu on 12.01.2017.
 */

@SuppressWarnings("serial") //With this annotation we are going to hide compiler warnings
@IgnoreExtraProperties
public class ImagesClass implements Serializable {

    private boolean gallery;
    private String imgUri;
    private int order;

    public ImagesClass(){

    }

    public ImagesClass(boolean gallery, String imgUri, int order) {
        this.gallery = gallery;
        this.imgUri = imgUri;
        this.order = order;
    }



    public String getImgUri() {
        return imgUri;
    }

    public void setImgUri(String imgUri) {
        this.imgUri = imgUri;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public boolean isGallery() {
        return gallery;
    }

    public void setGallery(boolean gallery) {
        this.gallery = gallery;
    }
}
