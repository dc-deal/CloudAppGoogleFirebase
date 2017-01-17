package net.livingrecordings.giggermainapp.giggerMainClasses;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by Franky on 11.12.2016.
 */
@SuppressWarnings("serial") //With this annotation we are going to hide compiler warnings
public class ItemClassLocal implements Serializable {

    private int quntity;
    private String color,notes;
    private HashMap<String,String> bands = new HashMap();

    private String dbKey; // nicht als db parameter

    public ItemClassLocal(int quntity, String color, String notes, String dbKey, HashMap<String,String> bands) {
        this.quntity = quntity;
        this.color = color;
        this.notes = notes;
        this.dbKey = dbKey;
        this.bands = bands;
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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public HashMap<String, String> getBands() {
        return bands;
    }

    public void setBands(HashMap<String, String> bands) {
        this.bands = bands;
    }
}
