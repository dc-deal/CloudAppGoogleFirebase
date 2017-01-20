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
public class BandClass extends GiggerRootClass
        implements Serializable {

    private String logo,founder,color,location,website,style,imgUrl;
    private Map<String,Boolean> users = new HashMap<>();
    private Map<String,Boolean> gigSchablonen = new HashMap<>();
    // Später auch die gigs und der klendar.
    //private Map<String,Boolean> gigs = new HashMap<>();

    public BandClass(){

    }

    public BandClass(String logo, String founder, String color, String location, String website, String style, String imgUrl, Map<String, Boolean> users, Map<String, Boolean> gigSchablonen) {
        this.logo = logo;
        this.founder = founder;
        this.color = color;
        this.location = location;
        this.website = website;
        this.style = style;
        this.imgUrl = imgUrl;
        this.users = users;
        this.gigSchablonen = gigSchablonen;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getFounder() {
        return founder;
    }

    public void setFounder(String founder) {
        this.founder = founder;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public Map<String, Boolean> getUsers() {
        return users;
    }

    public void setUsers(Map<String, Boolean> users) {
        this.users = users;
    }

    public Map<String, Boolean> getGigSchablonen() {
        return gigSchablonen;
    }

    public void setGigSchablonen(Map<String, Boolean> gigSchablonen) {
        this.gigSchablonen = gigSchablonen;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
