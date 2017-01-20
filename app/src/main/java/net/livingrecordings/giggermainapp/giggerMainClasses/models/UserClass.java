package net.livingrecordings.giggermainapp.giggerMainClasses.models;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Kraetzig Neu on 18.01.2017.
 */
@SuppressWarnings("serial") //With this annotation we are going to hide compiler warnings
@IgnoreExtraProperties
public class UserClass  extends GiggerRootClass
        implements Serializable{

    private String email,mobileNr,imgUrl;
    private Map<String,Boolean> bands = new HashMap<>();

    public UserClass(){

    }

    public UserClass(String email, String mobileNr, String imgUrl, Map<String, Boolean> bands){
        this.email = email;
        this.mobileNr = mobileNr;
        this.imgUrl = imgUrl;
        this.bands = bands;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobileNr() {
        return mobileNr;
    }

    public void setMobileNr(String mobileNr) {
        this.mobileNr = mobileNr;
    }

    public Map<String, Boolean> getBands() {
        return bands;
    }

    public void setBands(Map<String, Boolean> bands) {
        this.bands = bands;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
