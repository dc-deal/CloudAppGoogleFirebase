package net.livingrecordings.giggermainapp.giggerMainClasses.models;

import android.net.Uri;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Franky on 11.12.2016.
 */
@SuppressWarnings("serial") //With this annotation we are going to hide compiler warnings
@IgnoreExtraProperties
public class ItemClass implements Serializable {

    private String name, searchName, desc, parCat,createdBy,galleryPic;
    private HashMap<String,String> imgs = new HashMap<>();
    private HashMap<String,Boolean> tags = new HashMap();
    private boolean isPublished = false;
   // dann wird sie so abgespreichertn. vorteil: bilder die shcon da sind werden nicht neu hochgeladen.

    // extras...
    @Exclude
    private String key;
    @Exclude
    public ArrayList<ImagesClass> uploadImages = new ArrayList<>();


    public ItemClass() {
        // STD .must have for FB
    }

    // LIGHTWEIGHT FIREBASE CONSTRUCTOR...
    // NO FUNCTIONALITY HERE!!!
    public ItemClass(String name, String searchName, String desc, String parCat, String galleryPic, HashMap<String, String> imgs,
                     HashMap<String, Boolean> tags, String createdBy, boolean isPublished) {
        this.name = name;
        this.searchName = searchName;
        this.desc = desc;
        this.parCat = parCat;
        this.galleryPic = galleryPic;
        this.imgs = imgs;
        this.createdBy = createdBy;
        this.isPublished = isPublished;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getParCat() {
        return parCat;
    }

    public void setParCat(String parCat) {
        this.parCat = parCat;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public HashMap<String,String> getImgs() {
        return imgs;
    }

    public void setImgs(HashMap<String,String> imgs) {
        this.imgs = imgs;
    }

    public Map<String, Boolean> getTags() {
        return tags;
    }

    public void setTags(HashMap<String, Boolean> tags) {
        this.tags = tags;
    }

    // EXCLUDES
    @Exclude
    public String getTagsAsCommatex(Map<String,Boolean> inp){
        String res = "";
        for(String tag : inp.keySet()){
            res = res + tag + ',';
        }
        if (!res.isEmpty()) {
            StringBuilder sb = new StringBuilder(res);
            sb.deleteCharAt(sb.length() - 1);
            res = sb.toString();
        }
        return res;
    }

    @Exclude
    public Map<String,Boolean> setTagsAsCommatext(String inp){
        Map<String,Boolean> tags = new HashMap<String,Boolean>();
        String [] array = inp.split(",");
        if ((array.length > 0)&& (array[0].trim().equals(""))){
            array = new String[]{}; // split macht es ncith richtig gibt dann einen leeren value auf e1 aus.
        }
        for (int i = 0; i < array.length; i++) {
            tags.put(array[i],true);
        }
        return tags;
    }
    @Exclude
    public String getDbKey(){
        return this.key;
    }
    @Exclude
    public void setDbKey(String key){
        this.key= key;
    }

    public String getSearchName() {
        return searchName;
    }

    public void setSearchName(String searchName) {
        this.searchName = searchName;
    }

    public boolean isPublished() {
        return isPublished;
    }

    public void setPublished(boolean published) {
        isPublished = published;
    }

    public String getGalleryPic() {
        return galleryPic;
    }

    public void setGalleryPic(String galleryPic) {
        this.galleryPic = galleryPic;
    }
}
