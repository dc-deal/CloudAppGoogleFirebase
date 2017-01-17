package net.livingrecordings.giggermainapp.giggerMainClasses.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Franky on 11.12.2016.
 */
@SuppressWarnings("serial") //With this annotation we are going to hide compiler warnings
@IgnoreExtraProperties
public class ItemClassLocal implements Serializable {

    private int quntity;
    private String color,privatenotes,serNr,searchName;
    private Map<String,Boolean> bands = new HashMap();
    private Map<String,Boolean> tags = new HashMap();

    public ItemClassLocal() {
        // STD .must have for FB
    }

    public ItemClassLocal(int quntity, String color, String privatenotes, String serNr, String searchName){
        this.quntity = quntity;
        this.color = color;
        this.privatenotes = privatenotes;
        this.serNr = serNr;
        this.searchName = searchName;
    }

    @Exclude
    private String key;
    @Exclude
    public String getDbKey(){
        return this.key;
    }
    @Exclude
    public void setDbKey(String key){
        this.key= key;
    }

    public int getQuntity() {
        return quntity;
    }

    public void setQuntity(int quntity) {
        this.quntity = quntity;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getPrivatenotes() {
        return privatenotes;
    }

    public void setPrivatenotes(String privatenotes) {
        this.privatenotes = privatenotes;
    }

    public String getSerNr() {
        return serNr;
    }

    public void setSerNr(String serNr) {
        this.serNr = serNr;
    }

    public Map<String, Boolean> getBands() {
        return bands;
    }

    public void setBands(Map<String, Boolean> bands) {
        this.bands = bands;
    }

    public Map<String, Boolean> getTags() {
        return tags;
    }

    public void setTags(Map<String, Boolean> tags) {
        this.tags = tags;
    }

    public String getSearchName() {
        return searchName;
    }

    public void setSearchName(String searchName) {
        this.searchName = searchName;
    }
}
