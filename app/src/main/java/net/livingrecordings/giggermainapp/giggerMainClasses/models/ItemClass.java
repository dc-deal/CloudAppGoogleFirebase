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
public class ItemClass extends GiggerRootClass
        implements Serializable {

    private String searchName, desc, createdBy;
    private HashMap<String,Boolean> tags = new HashMap();
    private boolean isPublished = false;


    public ItemClass() {
    }

    public ItemClass(String searchName, String desc,
                     HashMap<String, Boolean> tags, String createdBy, boolean isPublished) {
        this.searchName = searchName;
        this.desc = desc;
        this.createdBy = createdBy;
        this.isPublished = isPublished;
    }




    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
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

}
