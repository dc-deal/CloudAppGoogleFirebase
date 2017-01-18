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
public class UserClass implements Serializable{

    private String userName,email,mobileNr;
    private Map<String,Boolean> bands = new HashMap<>();

    public UserClass(){

    }

    public UserClass(String userName, String email, String mobileNr,Map<String,Boolean> bands){
        this.userName = userName;
        this.email = email;
        this.mobileNr = mobileNr;
        this.bands = bands;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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
}
